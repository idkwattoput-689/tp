package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.getTypicalBrotherGym;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.BrotherGym;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.model.workoutplan.WorkoutDate;
import seedu.address.model.workoutplan.WorkoutDescription;
import seedu.address.model.workoutplan.WorkoutPlan;
import seedu.address.model.workoutplan.WorkoutPlansList;
import seedu.address.model.workoutplan.WorkoutTitle;

/** Contains unit and integration tests for {@link EditPlanCommand}. */
public class EditPlanCommandTest {

    private static final WorkoutPlan FIRST_PLAN = new WorkoutPlan(
            new WorkoutTitle("Strength"), new WorkoutDescription("Upper body"),
            WorkoutDate.fromLocalDate(LocalDate.of(2026, 10, 15)));
    private static final WorkoutPlan SECOND_PLAN = new WorkoutPlan(
            new WorkoutTitle("Cardio"), new WorkoutDescription("Running"),
            WorkoutDate.fromLocalDate(LocalDate.of(2026, 10, 16)));

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new EditPlanCommand(null, new EditPlanCommand.EditPlanDescriptor()));
        assertThrows(NullPointerException.class, () ->
                new EditPlanCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void execute_someFieldsSpecified_editsPlanAndPreservesOtherFields() {
        Model model = createModelWithPlans(FIRST_PLAN, SECOND_PLAN);
        Student currentStudent = model.getFilteredStudentList().get(0);
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated strength"));
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1), descriptor);
        command.setCurrentStudent(currentStudent);

        WorkoutPlan expectedPlan = new WorkoutPlan(new WorkoutTitle("Updated strength"),
                FIRST_PLAN.getWorkouts(), FIRST_PLAN.getDate());
        Student expectedStudent = new Student(currentStudent.getName(), currentStudent.getPhone(),
                currentStudent.getEmail(), currentStudent.getAddress(), currentStudent.getTags(),
                new WorkoutPlansList(List.of(expectedPlan, SECOND_PLAN)));
        Model expectedModel = new ModelManager(new BrotherGym(model.getBrotherGym()), new UserPrefs());
        expectedModel.setStudent(currentStudent, expectedStudent);

        String expectedMessage = String.format(EditPlanCommand.MESSAGE_SUCCESS, expectedPlan);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedStudent, command.getCurrentStudent().orElseThrow());
    }

    @Test
    public void execute_allFieldsSpecified_editsPlan() {
        Model model = createModelWithPlans(FIRST_PLAN);
        Student currentStudent = model.getFilteredStudentList().get(0);
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated plan"));
        descriptor.setDescription(new WorkoutDescription("Updated description"));
        descriptor.setDate(WorkoutDate.fromLocalDate(LocalDate.of(2026, 12, 1)));
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1), descriptor);
        command.setCurrentStudent(currentStudent);

        WorkoutPlan expectedPlan = new WorkoutPlan(new WorkoutTitle("Updated plan"),
                new WorkoutDescription("Updated description"),
                WorkoutDate.fromLocalDate(LocalDate.of(2026, 12, 1)));
        Student expectedStudent = new Student(currentStudent.getName(), currentStudent.getPhone(),
                currentStudent.getEmail(), currentStudent.getAddress(), currentStudent.getTags(),
                new WorkoutPlansList(List.of(expectedPlan)));
        Model expectedModel = new ModelManager(new BrotherGym(model.getBrotherGym()), new UserPrefs());
        expectedModel.setStudent(currentStudent, expectedStudent);

        CommandTestUtil.assertCommandSuccess(command, model,
                String.format(EditPlanCommand.MESSAGE_SUCCESS, expectedPlan), expectedModel);
    }

    @Test
    public void execute_noCurrentStudent_throwsCommandException() {
        Model model = new ModelManager(getTypicalBrotherGym(), new UserPrefs());
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated plan"));

        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1), descriptor);

        CommandTestUtil.assertCommandFailure(command, model, EditPlanCommand.MESSAGE_NO_STUDENT_SELECTED);
    }

    @Test
    public void execute_invalidWorkoutId_throwsCommandException() {
        Model model = createModelWithPlans(FIRST_PLAN);
        Student currentStudent = model.getFilteredStudentList().get(0);
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated plan"));
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(2), descriptor);
        command.setCurrentStudent(currentStudent);

        CommandTestUtil.assertCommandFailure(command, model, EditPlanCommand.MESSAGE_INVALID_WORKOUT_ID);
    }

    @Test
    public void execute_noFieldsSpecified_throwsCommandException() {
        Model model = createModelWithPlans(FIRST_PLAN);
        Student currentStudent = model.getFilteredStudentList().get(0);
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1),
                new EditPlanCommand.EditPlanDescriptor());
        command.setCurrentStudent(currentStudent);

        CommandTestUtil.assertCommandFailure(command, model, EditPlanCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void execute_duplicatePlan_throwsCommandException() {
        Model model = createModelWithPlans(FIRST_PLAN, SECOND_PLAN);
        Student currentStudent = model.getFilteredStudentList().get(0);
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(SECOND_PLAN.getTitle());
        descriptor.setDescription(SECOND_PLAN.getWorkouts());
        descriptor.setDate(SECOND_PLAN.getDate());
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1), descriptor);
        command.setCurrentStudent(currentStudent);

        CommandTestUtil.assertCommandFailure(command, model, EditPlanCommand.MESSAGE_DUPLICATE_PLAN);
    }

    @Test
    public void equals() {
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated plan"));
        EditPlanCommand first = new EditPlanCommand(Index.fromOneBased(1), descriptor);
        EditPlanCommand copy = new EditPlanCommand(Index.fromOneBased(1), descriptor);
        EditPlanCommand different = new EditPlanCommand(Index.fromOneBased(2), descriptor);

        assertTrue(first.equals(first));
        assertTrue(first.equals(copy));
        assertFalse(first.equals(different));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
    }

    @Test
    public void toStringMethod() {
        EditPlanCommand.EditPlanDescriptor descriptor = new EditPlanCommand.EditPlanDescriptor();
        descriptor.setTitle(new WorkoutTitle("Updated plan"));
        EditPlanCommand command = new EditPlanCommand(Index.fromOneBased(1), descriptor);

        String expected = EditPlanCommand.class.getCanonicalName() + "{targetIndex=" + Index.fromOneBased(1)
                + ", editPlanDescriptor="
                + descriptor + "}";
        assertEquals(expected, command.toString());
    }

    private static Model createModelWithPlans(WorkoutPlan... plans) {
        Student baseStudent = new ModelManager(getTypicalBrotherGym(), new UserPrefs())
                .getFilteredStudentList().get(0);
        Student studentWithPlans = new Student(baseStudent.getName(), baseStudent.getPhone(), baseStudent.getEmail(),
                baseStudent.getAddress(), baseStudent.getTags(), new WorkoutPlansList(List.of(plans)));
        BrotherGym brotherGym = new BrotherGym();
        brotherGym.addStudent(studentWithPlans);
        return new ModelManager(brotherGym, new UserPrefs());
    }
}
