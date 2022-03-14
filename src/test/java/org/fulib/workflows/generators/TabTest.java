package org.fulib.workflows.generators;

import org.junit.Test;

public class TabTest {
    @Test
    public void testTabRemoval() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromString(
                """
                        - workflow: Test
                        - event: data
                        \ttest: test
                         \s"""
        );
    }
}
