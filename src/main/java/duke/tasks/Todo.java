package duke.tasks;
/** A type of Task that does not contain any new information
 *
 * @author Wong Yong Xiang
 */
public class Todo extends Task{
    public Todo(String description) {
        super(description);
    }

    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /** returns the string representation of Todo
     *
     * @return string representation of Todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toDataFormatString() {
        int marked = 0;
        if(super.isDone) {
            marked = 1;
        }
        return "T / " + marked + " / " + super.description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Todo)) {
            return false;
        }

        Todo t = (Todo) o;
        if (this.description.equals(t.description)) {
            return true;
        } else {
            return false;
        }
    }
}
