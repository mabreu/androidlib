package br.com.datumti.library.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	private static final String TAG = "IMAGE LOADER"; 
	private boolean resizeImageOn = true;
	private boolean settingBackgroundImage = false;

	ImageLoaderListener listener;

	MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    Context context;

    public ImageLoader(Context ctx){
        this.context = ctx;
    	//Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        fileCache = new FileCache(ctx);
    }
	
    public ImageLoaderListener getListener() {
		return listener;
	}

	public void setListener(ImageLoaderListener listener) {
		this.listener = listener;
	}

    public void DisplayImage(String url, Activity activity, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        
        if(bitmap != null) {
        	final float scale = activity.getApplicationContext().getResources().getDisplayMetrics().density;
	        
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
	        Bitmap resizedBitmap;

	        if (resizeImageOn)
	        	resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        else
	        	resizedBitmap = bitmap;
	        
	        if (! settingBackgroundImage)
	        	imageView.setImageBitmap(resizedBitmap);
	        else
	        	imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
	        
            if (listener != null) {
            	listener.onLoad(imageView, bitmap.getWidth(), bitmap.getHeight());
            }
        } else {
            queuePhoto(url, activity, imageView);
        }    
    }
   
    private void queuePhoto(String url, Activity activity, ImageView imageView) {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
        photosQueue.Clean(imageView);
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        synchronized(photosQueue.photosToLoad) {
            photosQueue.photosToLoad.push( p );
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }
    
    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile( url );

        // FROM CACHE
        Bitmap b = loadImageCache( url );

        if ( null != b ) {
        	Log.i( TAG, "Carregou imagem do cache: " + url);
        	return b;
        }
        
        //from SD cache
        b = decodeFile( f );

        if( null != b ) {
        	Log.i( TAG, "Carregou imagem do SD cache: " + url);
        	return b;
        } 
        
        //from web
        try {
        	Log.i( TAG, "Carregou imagem da web: " + url);
        	
            Bitmap bitmap = null;
            URL imageUrl = new URL( url );
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            
            conn.setRequestProperty( "User-Agent", "" ); 
            conn.setConnectTimeout( 30000 );
            conn.setReadTimeout( 30000 );
            
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            StreamUtils.CopyStream(is, os);
            os.close();
            
            bitmap = decodeFile(f);
            saveImageCache( context, bitmap, url ); // salvar em cache            
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            return BitmapFactory.decodeStream( new FileInputStream( f ) );
        } catch( FileNotFoundException e ) {
        	e.printStackTrace();
        }

        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u; 
            imageView = i;
        }
    }
    
    PhotosQueue photosQueue = new PhotosQueue();
    
    public void stopThread() {
        photoLoaderThread.interrupt();
    }
    
    //stores list of photos to download
    class PhotosQueue {
        private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
        
        //removes all instances of this ImageView
        public void Clean(ImageView image) {
            for( int j = 0; j < photosToLoad.size(); ) {
                if( photosToLoad.get(j).imageView == image )
                    photosToLoad.remove(j);
                else
                    j++;
            }
        }
    }
    
    class PhotosLoader extends Thread {
        public void run() {
            try {
                while(true) {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size() == 0)
                        synchronized(photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();
                        }

                    if(photosQueue.photosToLoad.size() != 0) {
                        PhotoToLoad photoToLoad;

                        synchronized(photosQueue.photosToLoad){
                            photoToLoad = photosQueue.photosToLoad.pop();
                        }
                        
                        Bitmap bmp = getBitmap(photoToLoad.url);
                        memoryCache.put(photoToLoad.url, bmp);
                        String tag = imageViews.get(photoToLoad.imageView);

                        if(tag != null && tag.equals(photoToLoad.url)) {
                            Activity a = (Activity) photoToLoad.imageView.getContext();

                            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView, a);
                            a.runOnUiThread(bd);
                        }
                    }

                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }
    
    PhotosLoader photoLoaderThread=new PhotosLoader();
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        Activity activity;
        public BitmapDisplayer(Bitmap b, ImageView i, Activity a){bitmap=b;imageView=i;activity=a;}

        public void run() {
            if(bitmap!=null) {
            	final float scale = activity.getApplicationContext().getResources().getDisplayMetrics().density;
    	        
    			Matrix matrix = new Matrix();
    			matrix.postScale(scale, scale);
    	        Bitmap resizedBitmap;

    	        if (resizeImageOn)
    	        	resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    	        else
    	        	resizedBitmap = bitmap;

    	        if (!settingBackgroundImage)
    	        	imageView.setImageBitmap(resizedBitmap);
    	        else
    	        	imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    	        
                if (listener != null) {
                	listener.onLoad(imageView, bitmap.getWidth(), bitmap.getHeight());
                }
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    /**
	 * SALVE IMAGE CACHE
	 * 
	 * Metodo responsavel por salvar em cache uma imagem
	 */
	public static void saveImageCache( Context context, Bitmap bitmap, String url ) {
		try {
			String fileName = createPath(url, context);
			
			Log.i( TAG, "saveImageCache: " + fileName );
			
			List<String> fileList = Arrays.asList(fileName.split(("/")));
			String dirName = TextUtils.join("/", fileList.subList(0, (fileList.size() - 1)));
			
			File dirHnd = new File(dirName);
			dirHnd.mkdirs();
			
			File file = new File ( fileName );
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			
			if ( fileName.toLowerCase().indexOf("png") > 0 ) {
				bitmap.compress(  CompressFormat.PNG  , 100, bos);
			} else {
				bitmap.compress(  CompressFormat.JPEG  , 100, bos);
			}
			
			bos.flush();
	        bos.close();
		} catch (FileNotFoundException e) {
			Log.i( TAG, "ERROR: saveImageCache: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.i( TAG, "ERROR: saveImageCache: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			Log.i( TAG, "ERROR: saveImageCache: " + e);
			e.printStackTrace();
		}       		
	}
	
	/**
	 * LOAD IMAGE CACHE
	 * 
	 * Metodo responsavel por carregar uma imagem do cache, caso exista.
	 */
	public Bitmap loadImageCache( String url ){
		String path = createPath(url, context);
		Log.i( TAG, "loadImageCache: " + path );

		Bitmap bitmap = null;
		File file = new File( path );

    	if( file.exists() ) {
    		Log.i( TAG, "loadImageCache - Arquivo existe" );

			try {
				FileInputStream infile = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(infile);
				infile.close();
			} catch (FileNotFoundException e) {
				Log.i( TAG, "ERROR: loadImageCache: " + e);
				e.printStackTrace();
			} catch (IOException e) {
				Log.i( TAG, "ERROR: loadImageCache: " + e);
				e.printStackTrace();
			}
		}

    	return bitmap;
	}

	/**
	 * CREATE PATH
	 */
	public static String createPath (String filename, Context context) {
		String fileName = context.getCacheDir() + "/" + filename.replaceFirst("^[^:]+://", "");
		fileName = fileName.replaceAll("%", "");
		List<String> urlList = Arrays.asList(fileName.split("\\?"));

		if (urlList.size() > 1) {
			String toAppend = urlList.get(1).replaceAll("/", "_").replaceAll("=", "_");
			fileName = urlList.get(0) + toAppend;
		}

		return fileName;
	}

	public void setResizeImageOn(boolean resizeImageOn) {
		this.resizeImageOn = resizeImageOn;
	}

	public boolean isResizeImageOn() {
		return resizeImageOn;
	}

	public void setSettingBackgroundImage(boolean settingBackgroundImage) {
		this.settingBackgroundImage = settingBackgroundImage;
	}

	public boolean isSettingBackgroundImage() {
		return settingBackgroundImage;
	}
}
