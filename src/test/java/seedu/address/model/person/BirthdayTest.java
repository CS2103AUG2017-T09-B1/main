package seedu.address.model.person;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BirthdayTest {

    @Test
    public void equals() {
        Birthday birthday = new Birthday("01/02/1994");

        // same object -> returns true
        assertTrue(birthday.equals(birthday));

        // same values -> returns true
        Birthday birthdayCopy = new Birthday("01/02/1994");
        assertTrue(birthday.toString().equals(birthdayCopy.toString()));

        // different types -> returns false
        assertFalse(birthday.equals(1));

        // null -> returns false
        assertFalse(birthday.equals(null));

        // different person -> returns false
        Birthday differentRemark = new Birthday("02/01/1994");
        assertFalse(birthday.equals(differentRemark));
    }
}
