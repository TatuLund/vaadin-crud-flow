package com.vaadin.flow.component.crud.examples;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.SimpleCrudFilter;
import com.vaadin.flow.component.crud.SimpleCrudGrid;
import com.vaadin.flow.component.crud.Util;
import com.vaadin.flow.component.crud.annotation.Hidden;
import com.vaadin.flow.component.crud.annotation.Order;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Route(value = "")
@Theme(Lumo.class)
public class Home extends Div {

    // Dummy DB. A real app will hook up something like JPA.
    private static final Set<Person> DB = new LinkedHashSet<>(Arrays.asList(
            new Person("Mario", 1, new Stuff(2, "Mario")),
            new Person("Luigi", 2, new Stuff(2, "Luigi")),
            new Person("Sayo", 3, new Stuff(2, "Sayo")),
            new Person("Kingsley", 4, new Stuff(2, "Kingsley")),
            new Person("Dami", 5, new Stuff(2, "Dami")),
            new Person("Tobi", 6, new Stuff(2, "Tobi")),
            new Person("Andrew", 7, new Stuff(2, "Andrew")),
            new Person("Eno", 8, new Stuff(2, "Eno")),
            new Person("Isau", 9, new Stuff(2, "Isau")),
            new Person("Ronaldo", 10, new Stuff(1, "Ronaldo"))
    ));

    public Home() {
        Crud<Person> crud = new Crud<>(new SimpleCrudGrid<>(Person.class, true), new DummyCrudEditor<>());

        // Just a proof-of-concept.
        crud.setDataProvider(new AbstractBackEndDataProvider<Person, SimpleCrudFilter>() {

            @Override
            protected Stream<Person> fetchFromBackEnd(Query<Person, SimpleCrudFilter> query) {
                int offset = query.getOffset();
                int limit = query.getLimit();

                Stream<Person> stream = DB.stream();

                if (query.getFilter().isPresent()) {
                    stream = stream
                            .filter(predicate(query.getFilter().get()))
                            .sorted(comparator(query.getFilter().get()));
                }

                return stream.skip(offset).limit(limit);
            }

            @Override
            protected int sizeInBackEnd(Query<Person, SimpleCrudFilter> query) {
                return (int) fetchFromBackEnd(query).count();
            }

            private Predicate<Person> predicate(SimpleCrudFilter filter) {
                // For RDBMS just generate a WHERE clause
                return filter.getConstraints().entrySet().stream()
                        .map(constraint -> (Predicate<Person>) person -> {
                            try {
                                Object value = valueOf(constraint.getKey(), person);
                                return value != null && value.toString().startsWith(constraint.getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        })
                        .reduce(Predicate::and)
                        .orElse(e -> true);
            }

            private Comparator<Person> comparator(SimpleCrudFilter filter) {
                // For RDBMS just generate an ORDER BY clause
                return filter.getSortOrders().entrySet().stream()
                        .map(sortClause -> {
                            try {
                                Comparator<Person> comparator
                                        = Comparator.comparing(person ->
                                        (Comparable) valueOf(sortClause.getKey(), person));

                                if (sortClause.getValue() == SortDirection.DESCENDING) {
                                    comparator = comparator.reversed();
                                }

                                return comparator;
                            } catch (Exception ex) {
                                return (Comparator<Person>) (o1, o2) -> 0;
                            }
                        })
                        .reduce(Comparator::thenComparing)
                        .orElse((o1, o2) -> 0);
            }

            private Object valueOf(String fieldName, Person person) {
                try {
                    Field field = Person.class.getDeclaredField(fieldName);
                    Object value;
                    try {
                        Method getter = Util.getterFor(field, Person.class);
                        value = getter.invoke(person);
                    } catch (Exception ex) {
                        field.setAccessible(true);
                        value = field.get(person);
                    }

                    return value;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        add(crud);
    }

    public static class Person {

        @Order(1)
        private String name;

        @Order
        private int age;

        @Hidden
        private Stuff stuff;

        private static String staticProp = "This should not show";

        public Person() {
        }

        public Person(String name, int age, Stuff stuff) {
            this.name = name;
            this.age = age;
            this.stuff = stuff;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Stuff getStuff() {
            return stuff;
        }

        public void setStuff(Stuff stuff) {
            this.stuff = stuff;
        }

        public static String getStaticProp() {
            return staticProp;
        }

        public static void setStaticProp(String staticProp) {
            Person.staticProp = staticProp;
        }
    }

    public static class Stuff {

        private int thingId;
        private String thing;

        public Stuff(int thingId, String thing) {
            this.thingId = thingId;
            this.thing = thing;
        }

        public int getThingId() {
            return thingId;
        }

        public void setThingId(int thingId) {
            this.thingId = thingId;
        }

        public String getThing() {
            return thing;
        }

        public void setThing(String thing) {
            this.thing = thing;
        }

        @Override
        public String toString() {
            return thingId + " - " + thing;
        }
    }

    @Tag("div")
    public static class DummyCrudEditor<E> extends CrudEditor<E> {

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public boolean isDirty() {
            return false;
        }
    }
}
