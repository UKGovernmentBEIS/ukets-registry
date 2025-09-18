package gov.uk.ets.registry.api.document.management.service;

import gov.uk.ets.registry.api.document.management.domain.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DocumentUtilsTest {

    private final DocumentUtils utils = new DocumentUtils();

    @Test
    void testDuplicatesWithoutDuplicates() {
        // given
        Date date = Date.from(Instant.now());

        Document documentOne = createDocument("Document One", null, date);
        Document documentTwo = createDocument("Document Two", null, date);
        Document documentThree = createDocument("Document Three", null, date);

        // when
        boolean result = utils.hasDuplicates(List.of(documentOne, documentTwo, documentThree), Document::getName);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    void testDuplicatesWithDuplicates() {
        // given
        Date date = Date.from(Instant.now());

        Document documentOne = createDocument("Document One", null, date);
        Document documentTwo = createDocument("Document", null, date);
        Document documentThree = createDocument("Document", null, date);

        // when
        boolean result = utils.hasDuplicates(List.of(documentOne, documentTwo, documentThree), Document::getName);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    void testDuplicatesWithEmptyList() {
        // when
        boolean result = utils.hasDuplicates(List.of(), Document::getName);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    void testUpdateOrderingAlreadyOrderedList() {
        // given
        Date date = Date.from(Instant.now());

        Document documentOne = createDocument("Document One", 1, date);
        Document documentTwo = createDocument("Document Two", 2, date);
        Document documentThree = createDocument("Document Three", 3, date);

        List<Document> documents = Stream.of(documentOne, documentTwo, documentThree).collect(Collectors.toList());

        // when
        utils.updateOrdering(documents, Document::getPosition, Document::getUpdatedOn, Document::setPosition);

        // then
        Assertions.assertEquals(1, documentOne.getPosition());
        Assertions.assertEquals(2, documentTwo.getPosition());
        Assertions.assertEquals(3, documentThree.getPosition());
    }

    @Test
    void testUpdateOrderingWithNewOrderMoveForward() {
        // given
        Instant now = Instant.now();

        Document documentOne = createDocument("Document One", 1, Date.from(now.minusSeconds(1)));
        Document documentTwo = createDocument("Document Two", 1, Date.from(now));
        Document documentThree = createDocument("Document Three", 3, Date.from(now));

        List<Document> documents = Stream.of(documentOne, documentTwo, documentThree).collect(Collectors.toList());

        // when
        utils.updateOrdering(documents, Document::getPosition, Document::getUpdatedOn, Document::setPosition);

        // then
        Assertions.assertEquals(2, documentOne.getPosition());
        Assertions.assertEquals(1, documentTwo.getPosition());
        Assertions.assertEquals(3, documentThree.getPosition());
    }

    @Test
    void testUpdateOrderingWithNewOrderMoveBackward() {
        // given
        Instant now = Instant.now();

        Document documentOne = createDocument("Document One", 2, Date.from(now.minusSeconds(1)));
        Document documentTwo = createDocument("Document Two", 2, Date.from(now));
        Document documentThree = createDocument("Document Three", 3, Date.from(now));

        List<Document> documents = Stream.of(documentOne, documentTwo, documentThree).collect(Collectors.toList());

        // when
        utils.updateOrdering(documents, Document::getPosition, Document::getUpdatedOn, Document::setPosition);

        // then
        Assertions.assertEquals(1, documentOne.getPosition());
        Assertions.assertEquals(2, documentTwo.getPosition());
        Assertions.assertEquals(3, documentThree.getPosition());
    }

    @Test
    void testUpdateOrderingWithEmptyList() {
        Assertions.assertDoesNotThrow(() ->
            utils.updateOrdering(new ArrayList<>(), Document::getPosition, Document::getUpdatedOn, Document::setPosition));
    }

    private static Document createDocument(String name, Integer order, Date date) {
        Document document = new Document();
        document.setName(name);
        document.setPosition(order);
        document.setUpdatedOn(date);
        return document;
    }
}
