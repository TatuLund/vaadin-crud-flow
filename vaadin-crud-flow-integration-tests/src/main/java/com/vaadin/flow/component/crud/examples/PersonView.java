package com.vaadin.flow.component.crud.examples;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "")
@Theme(Lumo.class)
@BodySize(height = "100vh", width = "100vw")
public class PersonView extends Div {

    public PersonView() {
        CrudEditor<Person> editor = createPersonEditor();

        Crud<Person> crud = new Crud<>(Person.class, editor);

        PersonCrudDataProvider dataProvider = new PersonCrudDataProvider();
        dataProvider.setSizeChangeListener(count ->
                crud.setFooter(String.format("%d item%s available", count, count == 1L ? "" : "s")));

        crud.setDataProvider(dataProvider);

        crud.addSaveListener(e -> dataProvider.persist(e.getItem()));
        crud.addDeleteListener(e -> dataProvider.delete(e.getItem()));

        setHeight("100%");
        add(crud);
    }

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
}
