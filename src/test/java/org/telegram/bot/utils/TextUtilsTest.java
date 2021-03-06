package org.telegram.bot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.telegram.bot.utils.TextUtils.*;

class TextUtilsTest {

    @org.junit.jupiter.api.Test
    void reduceSpacesTest() {
        String textExample = "test\n\n\n\ntest1    test2";

        assertEquals("test\ntest1 test2", TextUtils.reduceSpaces(textExample));
    }

    @Test
    void getPotentialCommandInTextTest() {
        String textWithSlash = "/bot";
        String textRussian = "Бот, как дела";
        String commonText = "погода Ростов-на-Дону";

        assertEquals(getPotentialCommandInText(textWithSlash), "bot");
        assertEquals(getPotentialCommandInText(textRussian), "бот");
        assertEquals(getPotentialCommandInText(commonText), "погода");
    }

    @Test
    void cutMarkdownSymbolsInTextTest() {
        String text = "*test1* _test2_ `test3` [test4](test5)";
        assertEquals(cutMarkdownSymbolsInText(text), "test1 test2 test3 test4test5");
    }

    @Test
    void cutHtmlTagsTest() {
        String text = "<a href=\"example.com\">test</a>";
        assertEquals(cutHtmlTags(text), "test");
    }
}