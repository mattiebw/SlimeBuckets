package dev.mattware.slimebuckets;

public interface PlayerCustomData {
    default boolean isInSlimeChunk() {
        return false;
    }

    default void setIsInSlimeChunk(boolean newValue) { }
}
