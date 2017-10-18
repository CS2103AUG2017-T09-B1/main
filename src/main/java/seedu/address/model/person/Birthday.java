package seedu.address.model.person;

/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is always valid
 */
public class Birthday {

    public final String value;

    public Birthday(String birthday) {
        this.value = birthday;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Birthday // instanceof handles nulls
                && this.value.equals(((Birthday) other).value)); // state check
           }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String day;
    public String month;
    public String year;
    public String age;

    public String getAge() {
        day =  this.value.substring(0,2);
        month = this.value.substring(3,5);
        year = this.value.substring(6,10);
        age = year + month + day;

        return age;
    }

}