package com.vaadin.flow.component.crud.examples.basic;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.crud.examples.Person;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.vaadin.flow.component.crud.examples.basic.PersonHelper.createPersonEditor;

@Route(value = "BasicUse")
@Theme(Lumo.class)
@BodySize(height = "100vh", width = "100vw")
public class BasicUseView extends VerticalLayout {

    public BasicUseView() {
        final Crud<Person> crud = new Crud<>(Person.class, createPersonEditor());

        final PersonCrudDataProvider dataProvider = new PersonCrudDataProvider();
        dataProvider.setSizeChangeListener(count ->
                crud.setFooter(String.format("%d items available", count)));

        crud.setDataProvider(dataProvider);

        final VerticalLayout eventsPanel = new VerticalLayout();
        eventsPanel.setId("events");

        final Button showFiltersButton = new Button("Show filter", event -> {
            CrudGrid<Person> grid = (CrudGrid<Person>) crud.getGrid();
            CrudFilter filter = grid.getFilter();
            String filterString = filter.getConstraints().toString()
                    + filter.getSortOrders().toString();

            Span entry = new Span();
            entry.setText(filterString);
            eventsPanel.add(entry);
        });
        showFiltersButton.setId("showFilter");

        final Button updateI18nButton = new Button("Switch to Yoruba",
                event -> crud.setI18n(createYorubaI18n()));
        updateI18nButton.setId("updateI18n");

        add(crud, showFiltersButton, updateI18nButton, eventsPanel);
    }

    private CrudI18n createYorubaI18n() {
        CrudI18n yorubaI18n = CrudI18n.createDefault();

        yorubaI18n.setNewItem("Eeyan titun");
        yorubaI18n.setEditItem("S'atunko eeyan");
        yorubaI18n.setSave("Fi pamo");
        yorubaI18n.setCancel("Fa'gi lee");
        yorubaI18n.setDelete("Paare");

        yorubaI18n.getConfirm().getCancel().setHeader("Akosile");
        yorubaI18n.getConfirm().getCancel().setMessage("Akosile ti a o tii fi pamo nbe");
        yorubaI18n.getConfirm().getCancel().getButton().setCancel("Se atunko sii");
        yorubaI18n.getConfirm().getCancel().getButton().setOk("Fa'gi lee");

        yorubaI18n.getConfirm().getDelete().setHeader("Amudaju ipare");
        yorubaI18n.getConfirm().getDelete().setMessage("Se o da o l'oju pe o fe pa eeyan yi re? Igbese yi o l'ayipada o.");
        yorubaI18n.getConfirm().getDelete().getButton().setCancel("Da'wo duro");
        yorubaI18n.getConfirm().getDelete().getButton().setOk("Paare");

        return yorubaI18n;
    }
}