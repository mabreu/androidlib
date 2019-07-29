package br.com.datumti.library.database;

import br.com.datumti.library.database.DbField.FieldType;

public class DbDomain extends DbObject {
	public enum DomainDefinition { ddUndefined, ddNo, ddYes };

	private FieldType fieldType;
	private int size;
	private int precision;

	private DomainDefinition autoInc;
	private DomainDefinition notNull;
	private DomainDefinition nullIfEmpty;
	private DomainDefinition readOnly;
	private DomainDefinition isKey;
	private DomainDefinition isUnique;
	private DomainDefinition roundValue;

	public DbDomain() {
		this( FieldType.ftString );
	}

	public DbDomain(FieldType fieldType) {
		this( fieldType, DomainDefinition.ddNo, DomainDefinition.ddNo, 0, DomainDefinition.ddNo, DomainDefinition.ddYes, 
				DomainDefinition.ddNo, -1, DomainDefinition.ddNo, DomainDefinition.ddNo );
	}

	public DbDomain( FieldType fieldType, DomainDefinition isKey, DomainDefinition notNull, int size, DomainDefinition autoInc, DomainDefinition nullIfEmpty,  
			DomainDefinition readOnly, int precision, DomainDefinition roundValue, DomainDefinition isUnique ) {
		this.fieldType = fieldType;
		this.isKey = isKey;
		this.notNull = notNull;
		this.size = size;
		this.autoInc = autoInc;
		this.nullIfEmpty = nullIfEmpty;
		this.readOnly = readOnly;
		this.precision = precision;
		this.roundValue = roundValue;
		this.isUnique = isUnique;
	}

	public static boolean DDOrDef( DomainDefinition dd, boolean def ) {
		if( dd == DomainDefinition.ddUndefined )
		   return def;

		return dd == DomainDefinition.ddYes;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public DomainDefinition getNotNull() {
		return notNull;
	}

	public void setNotNull(DomainDefinition notNull) {
		this.notNull = notNull;
	}

	public DomainDefinition getAutoInc() {
		return autoInc;
	}

	public void setAutoInc(DomainDefinition autoInc) {
		this.autoInc = autoInc;
	}

	public DomainDefinition getNullIfEmpty() {
		return nullIfEmpty;
	}

	public void setNullIfEmpty(DomainDefinition nullIfEmpty) {
		this.nullIfEmpty = nullIfEmpty;
	}

	public DomainDefinition getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(DomainDefinition readOnly) {
		this.readOnly = readOnly;
	}

	public DomainDefinition getIsKey() {
		return isKey;
	}

	public void setIsKey(DomainDefinition isKey) {
		this.isKey = isKey;
	}

	public DomainDefinition getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(DomainDefinition isUnique) {
		this.isUnique = isUnique;
	}

	public DomainDefinition getRoundValue() {
		return roundValue;
	}

	public void setRoundValue(DomainDefinition roundValue) {
		this.roundValue = roundValue;
	}
}
