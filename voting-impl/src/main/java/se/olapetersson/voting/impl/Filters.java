package se.olapetersson.voting.impl;

import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;

public class Filters extends DefaultHttpFilters {

    @Inject
    public Filters(CORSFilter filter) {
        super(filter);
    }
}
