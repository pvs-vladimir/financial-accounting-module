package finance.domain;

public class Category {
    private static int nextId = 0;

    private final int id;
    public final CategoryType type;
    public final String name;

    public Category(CategoryType type, String name) {
        this.id = nextId++;
        this.type = type;
        this.name = name;
    }

    public int getId() { return id; }
    public CategoryType getType() { return type; }
    public String getName() { return name; }
}
