package com.vaadin.flow.component.crud.vaadincom;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route("vaadin-crud")
public class CrudView extends DemoView {

    @Override
    protected void initView() {
        createListOfEntities();
        createCustomGrid();

        addCard("Crud examples objects",
                new Label("These objects are used in the examples above"));
    }

    private void createListOfEntities() {
        // begin-source-example
        // source-example-heading: Basic CRUD
        final Crud<Person> crud = new Crud<>(Person.class, createPersonEditor());

        final PersonDataProvider dataProvider = new PersonDataProvider();
        dataProvider.setSizeChangeListener(count -> crud.setFooter("Total: " + count));

        crud.getGrid().removeColumnByKey("id");
        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> dataProvider.persist(e.getItem()));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));
        // end-source-example

        addCard("Basic CRUD", crud);
    }

    private void createCustomGrid() {
        // begin-source-example
        // source-example-heading: CRUD with custom Grid
        final Grid<Person> grid = new Grid<>();
        final Crud<Person> crud = new Crud<>(Person.class, grid, createPersonEditor());

        final PersonDataProvider dataProvider = new PersonDataProvider();
        crud.setDataProvider(dataProvider);
        crud.addSaveListener(e -> dataProvider.persist(e.getItem()));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));

        Crud.addEditColumn(grid);
        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getLastName).setHeader("Last Name");
        // end-source-example

        addCard("CRUD with custom Grid", crud);
    }

    // begin-source-example
    // source-example-heading: Crud examples objects
    /**
     * The Person entity object.
     */
    public static class Person implements Cloneable {
        private Integer id;
        private String firstName;
        private String lastName;

        public Person(Integer id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public Person clone() {
            try {
                return (Person)super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    /**
     * Creates an editor for the Person entity
     */
    private CrudEditor<Person> createPersonEditor() {
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        FormLayout form = new FormLayout(firstName, lastName);

        Binder<Person> binder = new Binder<>(Person.class);
        binder.bind(firstName, Person::getFirstName, Person::setFirstName);
        binder
                .forField(lastName)
                .withValidator(
                        value -> value != null && value.startsWith("O"),
                        "Only last names starting with 'O' allowed")
                .bind(Person::getLastName, Person::setLastName);

        return new BinderCrudEditor<>(binder, form);
    }
    // end-source-example
}
