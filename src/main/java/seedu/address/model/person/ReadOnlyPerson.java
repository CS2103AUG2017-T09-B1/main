package seedu.address.model.person;

import java.util.Comparator;

import java.util.Set;

import javafx.beans.property.ObjectProperty;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * A read-only immutable interface for a Person in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyPerson {

    ObjectProperty<Name> nameProperty();
    Name getName();
    ObjectProperty<Phone> phoneProperty();
    Phone getPhone();
    ObjectProperty<Email> emailProperty();
    Email getEmail();
    ObjectProperty<Address> addressProperty();
    Address getAddress();
    ObjectProperty<Birthday> birthdayProperty();
    Birthday getBirthday();
    ObjectProperty<UniqueTagList> tagProperty();
    Set<Tag> getTags();

    static String getNewStringAgeFormat(ReadOnlyPerson person) {
        if (person.getBirthday().toString().equals("")) {
            return "";
        } else {
            String numInString = person.getBirthday().toString();    // Converts birthday to String type
            String dayForNum = numInString.substring(0, 2);        // Index of day in dd/mm/yyyy
            String monthForNum = numInString.substring(3, 5);      // Index of month in dd/mm/yyyy
            String yearForNum = numInString.substring(6, 10);      // Index of year in dd/mm/yyyy
            return yearForNum + monthForNum + dayForNum;           // Return string format yyyymmdd
        }
    }

    static String getNewStringBirthdayFormat(ReadOnlyPerson person) {
        if (person.getBirthday().toString().equals("")) {
            return "";
        } else {
            String numInString = person.getBirthday().toString();  // Converts birthday to String type
            String dayForNum = numInString.substring(0, 2);        // Index of day in dd/mm/yyyy
            String monthForNum = numInString.substring(3, 5);      // Index of month in dd/mm/yyyy
            String yearForNum = numInString.substring(6, 10);      // Index of year in dd/mm/yyyy
            return monthForNum + dayForNum + yearForNum;           // Return String format mmddyyy
        }
    }

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyPerson other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getPhone().equals(this.getPhone())
                && other.getEmail().equals(this.getEmail())
                && other.getAddress().equals(this.getAddress())
                && other.getBirthday().equals(this.getBirthday()));
    }

    /**
     * Formats the person as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Phone: ")
                .append(getPhone())
                .append(" Email: ")
                .append(getEmail())
                .append(" Address: ")
                .append(getAddress())
                .append(" Birthday: ")
                .append(getBirthday())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

    Comparator<ReadOnlyPerson> COMPARE_BY_AGE = new Comparator<ReadOnlyPerson>() {
        public int compare(ReadOnlyPerson firstNum, ReadOnlyPerson secondNum) {
            String newFirstNum = getNewStringAgeFormat(firstNum);
            String newSecondNum = getNewStringAgeFormat(secondNum);
            if (newFirstNum.equals("") || newSecondNum.equals("")) {
                return newSecondNum.compareTo(newFirstNum);
            } else {
                return newFirstNum.compareTo(newSecondNum);
            }
        }
    };

    Comparator<ReadOnlyPerson> COMPARE_BY_BIRTHDAY = new Comparator<ReadOnlyPerson>() {
        public int compare(ReadOnlyPerson firstPerson, ReadOnlyPerson secondPerson) {
            String newFirstNum = getNewStringBirthdayFormat(firstPerson);
            String newSecondNum = getNewStringBirthdayFormat(secondPerson);
            if (newFirstNum.equals("") || newSecondNum.equals("")) {
                return newSecondNum.compareTo(newFirstNum);
            } else {
                return newFirstNum.compareTo(newSecondNum);
            }
        }
    };

}
