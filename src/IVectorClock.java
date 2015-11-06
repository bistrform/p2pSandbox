import java.util.HashMap;
import java.util.Map;

public interface IVectorClock
{
    /**
     * increments vector clock of the
     * @param pUnit
     */
    public void incrementClock(String pUnit);

    public Integer get(Object key);

    @Override
    public String toString();

    /**
     * VectorClock merging operation. Creates a new VectorClock with the maximum for
     * each element in either clock. Used in Buffer and Process to manipulate clocks.
     *
     * @param pOne - First Clock being merged.
     * @param pTwo - Second Clock being merged.
     *
     * @return A new VectorClock with the maximum for each element in either clock.
     */
    public VectorClock max(VectorClock pOne, VectorClock pTwo);

    /**
     *
     * MUST RETURN ENUM
     *
     * VectorClock compare operation. Returns one of four possible values indicating how
     * clock one relates to clock two:
     *
     * VectorClock.GREATER			If One > Two.
     * VectorClock.EQUAL			If One = Two.
     * VectorClock.SMALLER			If One < Two.
     * VectorClock.SIMULTANEOUS	    If One <> Two.
     *
     * @param pOne - First Clock being compared.
     * @param pTwo - Second Clock being compared.
     *
     * @return VectorComparison value indicating how One relates to Two.
     */
    public int compare(VectorClock pOne, VectorClock pTwo);
}