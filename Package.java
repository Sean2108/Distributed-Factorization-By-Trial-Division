/**
 * @author Sean Tan
 */

import java.io.Serializable;

/**
 * class for transferring start and end points
 * of search spaces
 */
public class Package implements Serializable {

    protected long start;
    protected long end;

    public Package(long start, long end) {
        this.start = start;
        this.end = end;
    }
}
