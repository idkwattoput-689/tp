package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PLAN;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.workoutplan.WorkoutPlan;
import seedu.address.model.workoutplan.WorkoutPlansList;

/**
 * Adds a workout plan to the Student currently being viewed.
 */
public class AddPlanCommand extends Command {

    public static final String COMMAND_WORD = "addPlan";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a workout plan to the Student currently being viewed.\n"
            + "Parameters: PLAN_TITLE " + PREFIX_PLAN + " DESCRIPTION " + PREFIX_DATE + " DATE(DDMMYYYY)\n"
            + "Example: " + COMMAND_WORD + " Strength " + PREFIX_PLAN + " Upper body " + PREFIX_DATE + " 081025";

    public static final String MESSAGE_SUCCESS = "New workout added: %1$s";
    public static final String MESSAGE_NO_STUDENT_SELECTED =
            "No student is currently selected. Use viewStudent first.";
    public static final String MESSAGE_DUPLICATE_PLAN = "This workout plan already exists.";

    private final WorkoutPlan toAdd;
    private Student currentStudent;
    private Student updatedStudent;

    /**
     * Creates a command to add the given workout plan to the current student.
     *
     * @param toAdd workout plan to add
     */
    public AddPlanCommand(WorkoutPlan toAdd) {
        requireNonNull(toAdd);
        this.toAdd = toAdd;
    }

    /** Supplies the student currently selected by the Logic layer. */
    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    /** Returns the updated student after successful execution. */
    @Override
    public Optional<Student> getCurrentStudent() {
        return Optional.ofNullable(updatedStudent);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (currentStudent == null) {
            throw new CommandException(MESSAGE_NO_STUDENT_SELECTED);
        }

        if (currentStudent.getWorkoutPlansList().getWorkoutList().contains(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PLAN);
        }

        WorkoutPlansList updatedPlans = currentStudent.getWorkoutPlansList().addWorkoutPlan(toAdd);
        updatedStudent = new Student(currentStudent.getName(), currentStudent.getPhone(),
                currentStudent.getEmail(), currentStudent.getAddress(), currentStudent.getTags(), updatedPlans);
        model.setStudent(currentStudent, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddPlanCommand otherAddPlanCommand)) {
            return false;
        }

        return toAdd.equals(otherAddPlanCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
