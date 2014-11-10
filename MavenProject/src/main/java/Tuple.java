import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
    private TupleDesc tupleDesc;
    private static final long serialVersionUID = 1L;

    private List<Field> fieldArr;
    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        this.tupleDesc = td;
        this.fieldArr = new ArrayList<Field>(Collections.<Field>nCopies(tupleDesc.numFields(), null));
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        return tupleDesc;
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        assert(i >= 0 && i < tupleDesc.numFields());
        fieldArr.set(i, f);
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        assert(i >= 0 && i < tupleDesc.numFields());
        return fieldArr.get(i);
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0 ; i < fieldArr.size(); i ++) {
            buffer.append(fieldArr.get(i).toString());
            if (i == fieldArr.size() - 1) {
                buffer.append("\n");
            } else {
                buffer.append("\t");
            }
        }
        return buffer.toString();
    }

    public Tuple clone() {
        Tuple cloneTuple = new Tuple(tupleDesc);
        Iterator<Field> iterator = this.fields();
        int counter = 0;
        while (iterator.hasNext()) {
            cloneTuple.setField(counter++,iterator.next());
        }
        return cloneTuple;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        return fieldArr.iterator();
    }
    
    /**
     * reset the TupleDesc of thi tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        this.tupleDesc = td;
    }
}
