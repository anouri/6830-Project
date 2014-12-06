/**
 * Created by trannguyen on 11/9/14.
 */
public class Type {
    public enum SupportedType {
        INT_TYPE("Integer"),
        STRING_TYPE("String");
        String rep;
        SupportedType(String rep) {
            this.rep = rep;
        }

        @Override
        public String toString() {
            return rep;
        }
    }

    SupportedType type;
    int length;

    /**
     * The Type of the column, currently support INT and STRING,
     * this constructor assume the default length of INT and STRING
     * @param type
     */
    public Type(SupportedType type) {
        this.type = type;
        if (type == SupportedType.INT_TYPE) {
            this.length = INT_LEN;
        } else if (type == SupportedType.STRING_TYPE) {
            this.length = STRING_LEN;
        }
    }

    /**
     * The Type of the column, currently support INT and STRING,
     * @param type INT_TYPE or STRING_TYPE
     * @param length how long the type is
     */
    public Type(SupportedType type,int length) {
        this(type);
        this.length = length;
    }

    private static int STRING_LEN = 128;
    private static int INT_LEN = 4;

    /**
     * @return the number of bytes required to store a field of this type.
     */
    public int getLen() {
        return length;
    }

    public SupportedType getType(){
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type)) return false;
        final Type that = (Type) o;
        return this.length == that.length && this.type == that.type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
