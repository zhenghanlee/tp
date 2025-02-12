package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.function.Predicate;

import seedu.address.commons.util.PredicateUtil;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ContainsKeywordsPredicate;
import seedu.address.model.person.ContainsKeywordsPredicate.PersonField;
import seedu.address.model.person.Person;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindCommand and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_TAG);
        Predicate<Person> nameQuery = argMultimap.getValue(PREFIX_NAME).map(q -> getPredicate(q, PersonField.NAME))
                .orElse(x -> true);
        Predicate<Person> phoneQuery = argMultimap.getValue(PREFIX_PHONE).map(q -> getPredicate(q, PersonField.PHONE))
                .orElse(x -> true);
        Predicate<Person> emailQuery = argMultimap.getValue(PREFIX_EMAIL).map(q -> getPredicate(q, PersonField.EMAIL))
                .orElse(x -> true);
        Predicate<Person> addressQuery = argMultimap.getValue(PREFIX_ADDRESS)
                .map(q -> getPredicate(q, PersonField.ADDRESS)).orElse(x -> true);
        Predicate<Person> tagQuery = argMultimap.getAllValues(PREFIX_TAG).stream()
                .map(t -> getPredicate(t, PersonField.TAG)).reduce(x -> true, Predicate::and);

        return new FindCommand(PredicateUtil.intersection(nameQuery, phoneQuery, emailQuery, addressQuery, tagQuery));
    }

    private Predicate<Person> getPredicate(String args, PersonField field) {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return p -> true;
        }

        String[] keywords = trimmedArgs.split("\\s+");
        return new ContainsKeywordsPredicate(Arrays.asList(keywords), field);
    }

}
