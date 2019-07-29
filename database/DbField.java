package br.com.datumti.library.database;

import java.util.Date;

public class DbField extends DbObject {
	public enum FieldType { ftNone, ftBoolean, ftInteger, ftDouble, ftString, ftDateTime, ftBlob };

	private Object value;
	private Object oldValue;

	private final String fieldName;
	private final FieldType fieldType;
	private String displayName;

	private int size;
	private int precision;

	private boolean autoInc;
	private boolean notNull;
	private boolean nullIfEmpty;
	private boolean readOnly;
	private boolean isKey;
	private boolean isUnique;
	private boolean roundValue;
	private boolean changed;

	public DbField( String fieldName, FieldType fieldType ) {
		this( fieldName, fieldType, fieldName, false, false, 0, false, true, false, -1, false, false );
	}

	public DbField( String fieldName, FieldType fieldType, int size ) {
		this( fieldName, fieldType, fieldName, false, false, size, false, true, false, -1, false, false );
	}

	public DbField( String fieldName, FieldType fieldType, DbDomain domain ) {
		this( fieldName, fieldType, fieldName, DbDomain.DDOrDef(domain.getIsKey(), false), DbDomain.DDOrDef(domain.getNotNull(), false), domain.getSize(),
				DbDomain.DDOrDef(domain.getAutoInc(), true), DbDomain.DDOrDef(domain.getNullIfEmpty(), true), DbDomain.DDOrDef(domain.getReadOnly(), false),
				domain.getPrecision(), DbDomain.DDOrDef(domain.getRoundValue(), false), DbDomain.DDOrDef(domain.getIsUnique(), false) );
	}

	public DbField( String fieldName, FieldType fieldType, String displayName, boolean isKey, boolean notNull, int size,
						boolean autoInc, boolean nullIfEmpty, boolean readOnly, int precision, boolean roundValue, boolean isUnique ) {
		this.changed = false;
		this.value = null;
		this.oldValue = null;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.displayName = displayName;
		this.isKey = isKey;
		this.autoInc = autoInc;
		this.notNull = notNull;
		this.size = size;
		this.nullIfEmpty = nullIfEmpty;
		this.readOnly = readOnly;
		this.precision = precision;
		this.roundValue = roundValue;
		this.isUnique = isUnique;
	}

	public String getFieldName() {
		return fieldName;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public DbField setNotNull(boolean notNull) {
		this.notNull = notNull;
		return this;
	}

	public boolean isAutoInc() {
		return autoInc;
	}

	public DbField setAutoInc(boolean autoInc) {
		this.autoInc = autoInc;
		return this;
	}

	public boolean isNullIfEmpty() {
		return nullIfEmpty;
	}

	public DbField setNullIfEmpty(boolean nullIfEmpty) {
		this.nullIfEmpty = nullIfEmpty;
		return this;
	}

	public DbField setSize(int size) {
		this.size = size;
		return this;
	}

	public int getSize() {
		return size;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public DbField setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public boolean isKey() {
		return isKey;
	}

	public DbField setKey(boolean isKey) {
		this.isKey = isKey;
		return this;
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean isNull() {
		return (value == null);
	}

	public boolean isUnique() {
		return isUnique;
	}

	public DbField setUnique(boolean isUnique) {
		this.isUnique = isUnique;
		return this;
	}

	public boolean isRoundValue() {
		return roundValue;
	}

	public DbField setRoundValue(boolean roundValue) {
		this.roundValue = roundValue;
		return this;
	}

	public int getPrecision() {
		return precision;
	}

	public DbField setPrecision(int precision) {
		this.precision = precision;
		return this;
	}

	public Object getOldValue() {
		return oldValue;
	}

	private void _setValue(Object value) {
		if( ! this.changed ) {
			this.oldValue = this.value;
			this.changed = true;
		}

		this.value = value;
	}

	public void setValue(long iValue) {
		_setValue( Long.valueOf( iValue ) );
	}

	public void setValue(boolean bValue) {
		_setValue( Boolean.valueOf( bValue ) );
	}

	public void setValue(double dValue) {
		_setValue( Double.valueOf( dValue ) );
	}

	public void setValue(Date tValue) {
		_setValue( tValue );
	}

	public void setValue(String sValue) {
		_setValue( sValue );
	}

	public void setNull() {
		_setValue( null );
	}

	public long asInteger() {
		return ((Long) value).longValue();
	}

	public boolean asBoolean() {
		return ((Boolean) value).booleanValue();
	}

	public double asDouble() {
		return ((Double) value).doubleValue();
	}

	public Date asDateTime() {
		return (Date) value;
	}

	public String asString() {
		return value.toString();
	}

	public String getValueScaped() {
		if( isNull() ) {
			return "NULL";
		}

		switch( fieldType ) {
		case ftBoolean:
		case ftString:
		case ftDateTime:
		case ftBlob:
			return "'" + asString() + "'";

		case ftInteger:
			return "" + asInteger();

		case ftDouble:
			return "" + asDouble();
		}

		return "'" + asString() + "'";
	}

	public String getOldValueEscaped() {
		if( getOldValue() == null ) {
			return "NULL";
		}

		switch( fieldType ) {
		case ftBoolean:
		case ftString:
		case ftDateTime:
		case ftBlob:
			return "'" + getOldValue() + "'";

		case ftInteger:
		case ftDouble:
			return "" + getOldValue();
		}

		return "'" + getOldValue() + "'";
	}
}
