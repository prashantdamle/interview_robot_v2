package com.nuix.interview.robot.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZoneTest {

    private final Zone zone = new Zone(10, 10);

    @Test
    @DisplayName("contains() accepts positions from 0,0 up to width-1,height-1")
    void containsPositionsInsideTheZone() {
        assertAll(
                () -> assertTrue(zone.contains(new Position(0, 0))),
                () -> assertTrue(zone.contains(new Position(9, 9))),
                () -> assertTrue(zone.contains(new Position(0, 9))),
                () -> assertTrue(zone.contains(new Position(9, 0)))
        );
    }

    @Test
    @DisplayName("contains() rejects positions just outside each edge")
    void rejectsPositionsOutsideTheZone() {
        assertAll(
                () -> assertFalse(zone.contains(new Position(-1, 0))),
                () -> assertFalse(zone.contains(new Position(0, -1))),
                () -> assertFalse(zone.contains(new Position(10, 9))),
                () -> assertFalse(zone.contains(new Position(9, 10)))
        );
    }

    @Test
    @DisplayName("width bounds x and height bounds y in a non-square zone")
    void widthBoundsXAndHeightBoundsY() {
        Zone wide = new Zone(5, 3);

        assertAll(
                () -> assertTrue(wide.contains(new Position(4, 2))),
                () -> assertFalse(wide.contains(new Position(5, 0))),
                () -> assertFalse(wide.contains(new Position(0, 3)))
        );
    }

    @Test
    @DisplayName("a zone must have a positive width and height")
    void rejectsNonPositiveDimensions() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Zone(0, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Zone(10, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Zone(-1, 10))
        );
    }
}
