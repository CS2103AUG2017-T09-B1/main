//@@author cqhchan
package seedu.address.logic.parser;

import org.junit.Test;
import seedu.address.logic.commands.LoginCommand;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import static seedu.address.logic.commands.CommandTestUtil.*;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParse;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

public class LoginCommandParserTest {
    private LoginCommandParser parser = new LoginCommandParser();


    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, LoginCommand.MESSAGE_USAGE);

        // missing username prefix
        assertParseFailure(parser, LoginCommand.COMMAND_WORD + VALID_USERNAME_PRIVATE
                + PASSWORD_DESC_PASSWORD , expectedMessage);

        // missing password prefix
        assertParseFailure(parser, LoginCommand.COMMAND_WORD + USERNAME_DESC_USERNAME
                + VALID_PASSWORD_PASSWORD , expectedMessage);

    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid username
        assertParse(parser, LoginCommand.COMMAND_WORD + INVALID_USERNAME_DESC
                + PASSWORD_DESC_PASSWORD , LoginCommand.MESSAGE_FAILURE);

        // invalid password
        assertParse(parser, LoginCommand.COMMAND_WORD + USERNAME_DESC_USERNAME
                + INVALID_PASSWORD_DESC , LoginCommand.MESSAGE_FAILURE);


    }
}