package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PLAN;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.workoutplan.WorkoutDate;
import seedu.address.model.workoutplan.WorkoutDescription;
import seedu.address.model.workoutplan.WorkoutPlan;
import seedu.address.model.workoutplan.WorkoutPlansList;
import seedu.address.model.workoutplan.WorkoutTitle;

/**
 * Edits a workout plan belonging to the Student currently being viewed.
 */
public class EditPlanCommand extends Command {

    public static final String COMMAND_WORD = "editPlan";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a workout plan from the Student currently being viewed.\n"
            + "Parameters: PLAN_ID [" + PREFIX_NAME + " PLAN_NAME] [" + PREFIX_PLAN + " DESCRIPTION] ["
            + PREFIX_DATE + " DATE(DDMMYYYY)]\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_NAME + " NewName " + PREFIX_PLAN + " New description "
            + PREFIX_DATE + " 15102026";

    public static final String MESSAGE_SUCCESS = "Edited workout: %1$s";
    public static final String MESSAGE_NO_STUDENT_SELECTED =
            "No student is currently selected. Use viewStudent first.";
    public static final String MESSAGE_INVALID_WORKOUT_ID =
            "Workout ID exceeded total number of workout plans.";
    public static final String MESSAGE_NOT_EDITED = "At least one workout-plan field must be provided.";
    public static final String MESSAGE_DUPLICATE_PLAN = "This workout plan already exists.";

    private final Index targetIndex;
    private final EditPlanDescriptor editPlanDescriptor;
    private Student currentStudent;
    private Student updatedStudent;

    /**
     * Creates a command to edit a workout plan.
     *
     * @param targetIndex index of the workout plan to edit
     * @param editPlanDescriptor fields to update
     */
    public EditPlanCommand(Index targetIndex, EditPlanDescriptor editPlanDescriptor) {
        requireNonNull(targetIndex);
        requireNonNull(editPlanDescriptor);
        this.targetIndex = targetIndex;
        this.editPlanDescriptor = new EditPlanDescriptor(editPlanDescriptor);
    }

    /** Supplies the student currently selected by the Logic layer. */
    @Override
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
        if (!editPlanDescriptor.isAnyFieldEdited()) {
            throw new CommandException(MESSAGE_NOT_EDITED);
        }

        List<WorkoutPlan> workoutPlans = currentStudent.getWorkoutPlansList().getWorkoutList();
        if (targetIndex.getZeroBased() < 0 || targetIndex.getZeroBased() >= workoutPlans.size()) {
            throw new CommandException(MESSAGE_INVALID_WORKOUT_ID);
        }

        WorkoutPlan planToEdit = workoutPlans.get(targetIndex.getZeroBased());
        WorkoutPlan editedPlan = createEditedPlan(planToEdit, editPlanDescriptor);

        if (workoutPlans.stream().anyMatch(plan -> !plan.equals(planToEdit) && plan.equals(editedPlan))) {
            throw new CommandException(MESSAGE_DUPLICATE_PLAN);
        }

        List<WorkoutPlan> updatedWorkoutPlans = new ArrayList<>(workoutPlans);
        updatedWorkoutPlans.set(targetIndex.getZeroBased(), editedPlan);
        WorkoutPlansList updatedPlans = new WorkoutPlansList(updatedWorkoutPlans);
        updatedStudent = new Student(currentStudent.getName(), currentStudent.getPhone(),
                currentStudent.getEmail(), currentStudent.getAddress(), currentStudent.getTags(), updatedPlans);
        model.setStudent(currentStudent, updatedStudent);

        return new CommandResult(String.format(MESSAGE_SUCCESS, editedPlan));
    }

    private static WorkoutPlan createEditedPlan(WorkoutPlan planToEdit, EditPlanDescriptor descriptor) {
        WorkoutTitle updatedTitle = descriptor.getTitle().orElse(planToEdit.getTitle());
        WorkoutDescription updatedDescription = descriptor.getDescription().orElse(planToEdit.getWorkouts());
        WorkoutDate updatedDate = descriptor.getDate().orElse(planToEdit.getDate());
        return new WorkoutPlan(updatedTitle, updatedDescription, updatedDate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditPlanCommand otherEditPlanCommand)) {
            return false;
        }
        return targetIndex.equals(otherEditPlanCommand.targetIndex)
                && editPlanDescriptor.equals(otherEditPlanCommand.editPlanDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("editPlanDescriptor", editPlanDescriptor)
                .toString();
    }

    /** Stores the optional fields to update on a workout plan. */
    public static class EditPlanDescriptor {
        private WorkoutTitle title;
        private WorkoutDescription description;
        private WorkoutDate date;

        /** Creates an empty descriptor. */
        public EditPlanDescriptor() {}

        /** Creates a copy of the given descriptor. */
        public EditPlanDescriptor(EditPlanDescriptor toCopy) {
            this.title = toCopy.title;
            this.description = toCopy.description;
            this.date = toCopy.date;
        }

        /** Returns whether at least one field is set. */
        public boolean isAnyFieldEdited() {
            return title != null || description != null || date != null;
        }

        /** Sets the replacement title. */
        public void setTitle(WorkoutTitle title) {
            this.title = requireNonNull(title);
        }

        /** Sets the replacement description. */
        public void setDescription(WorkoutDescription description) {
            this.description = requireNonNull(description);
        }

        /** Sets the replacement date. */
        public void setDate(WorkoutDate date) {
            this.date = requireNonNull(date);
        }

        /** Returns the replacement title, if supplied. */
        public Optional<WorkoutTitle> getTitle() {
            return Optional.ofNullable(title);
        }

        /** Returns the replacement description, if supplied. */
        public Optional<WorkoutDescription> getDescription() {
            return Optional.ofNullable(description);
        }

        /** Returns the replacement date, if supplied. */
        public Optional<WorkoutDate> getDate() {
            return Optional.ofNullable(date);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditPlanDescriptor otherDescriptor)) {
                return false;
            }
            return Objects.equals(title, otherDescriptor.title)
                    && Objects.equals(description, otherDescriptor.description)
                    && Objects.equals(date, otherDescriptor.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, date);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("title", title)
                    .add("description", description)
                    .add("date", date)
                    .toString();
        }
    }
}
