package com.vaadin.flow.component.crud;

/*
 * #%L
 * Vaadin Crud for Vaadin 10
 * %%
 * Copyright (C) 2018 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A simple grid implementation for Crud that allows searching and sorting backed by a data provider.
 *
 * @param <E> the bean type
 */
public class CrudGrid<E> extends Grid<E> {

    private final Class<E> beanType;
    private final boolean enableDefaultFilters;
    private final CrudFilter filter = new CrudFilter();
    private DataProvider<E, ?> dataProvider;

    /**
     * Instantiates a new CrudGrid for the supplied bean type.
     *
     * @param beanType the bean type
     * @param enableDefaultFilters true to enable filtering or false to disable
     */
    public CrudGrid(Class<E> beanType, boolean enableDefaultFilters) {
        super(beanType);

        this.beanType = beanType;
        this.enableDefaultFilters = enableDefaultFilters;

        setup();
    }

    private void setup() {
        if (enableDefaultFilters) {
            setupFiltering();
        }

        setupSorting();

        Crud.addEditColumn(this);
        setSelectionMode(SelectionMode.NONE);
    }

    private void setupFiltering() {
        final HeaderRow filterRow = this.appendHeaderRow();
        getColumns().forEach(column -> {
            final TextField field = new TextField();

            field.addValueChangeListener(event -> {
                filter.getConstraints().remove(column.getKey());

                if (!field.isEmpty()) {
                    filter.getConstraints().put(column.getKey(), event.getValue());
                }

                super.getDataProvider().refreshAll();
            });

            field.setValueChangeMode(ValueChangeMode.EAGER);

            filterRow.getCell(column).setComponent(field);
            field.setSizeFull();
            field.setPlaceholder("Filter");
        });
    }

    private void setupSorting() {
        setMultiSort(true);
        addSortListener(event -> {
            filter.getSortOrders().clear();
            event.getSortOrder().forEach(e ->
                    filter.getSortOrders().put(e.getSorted().getKey(), e.getDirection()));
            super.getDataProvider().refreshAll();
        });
    }

    /**
     * Returns the data provider set to the grid.
     *
     * @return the data provider of this grid, not {@code null}
     */
    @Override
    public DataProvider<E, ?> getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets a DataProvider&lt;E, CrudFilter&gt;
     *
     * @param dataProvider a {@link DataProvider}
     * @see CrudFilter
     * @throws IllegalArgumentException if the supplied data provider is not a DataProvider&lt;E, CrudFilter&gt;
     */
    @Override
    public void setDataProvider(DataProvider<E, ?> dataProvider) throws IllegalArgumentException {
        // Attempt a cast to ensure that the captured ? is actually a CrudFilter
        // Unfortunately this cannot be enforced by the compiler
        try {
            ConfigurableFilterDataProvider<E, Void, CrudFilter> provider
                    = ((DataProvider<E, CrudFilter>) dataProvider).withConfigurableFilter();

            provider.setFilter(filter);

            super.setDataProvider(provider);

            // Keep a reference to the original data provider being wrapped
            this.dataProvider = dataProvider;
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("DataProvider<" + beanType.getSimpleName()
                    + ", CrudFilter> expected", ex);
        }
    }
}
