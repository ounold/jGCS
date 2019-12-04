package executionTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EtMarkersChain {

    private static final String SEPARATOR = ".";

    private List<String> markers;

    private EtMarkersChain(String marker) {
        this.markers = Collections.singletonList(marker);
    }

    private EtMarkersChain(List<String> markers) {
        this.markers = markers;
    }

    public static EtMarkersChain init(String marker){
        return new EtMarkersChain(marker);
    }

    public EtMarkersChain get(String marker){
        List<String> markers = new ArrayList<>(this.markers);
        markers.add(marker);
        return new EtMarkersChain(markers);
    }

    @Override
    public String toString() {
        return String.join(SEPARATOR, markers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtMarkersChain that = (EtMarkersChain) o;
        return Objects.equals(toString(), that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }
}
