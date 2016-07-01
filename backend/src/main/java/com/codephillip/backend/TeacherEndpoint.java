package com.codephillip.backend;

/**
 * Created by codephillip on 7/1/16.
 */
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.codephillip.backend.OfyService.ofy;

/**
 * Created by codephillip on 7/1/16.
 */
@Api(name = "teacherEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.codephillip.com", ownerName = "backend.codephillip.com", packagePath=""))
public class TeacherEndpoint {

    // Make sure to add this endpoint to your web.xml file if this is a web application.

    private static final Logger LOG = Logger.getLogger(TeacherEndpoint.class.getName());

    public TeacherEndpoint() {
    }

    @ApiMethod(name = "listTeacher")
    public CollectionResponse<Teacher> listTeacher(@Nullable @Named("cursor") String cursorString,
                                                   @Nullable @Named("count") Integer count) {

        Query<Teacher> query = ofy().load().type(Teacher.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Teacher> records = new ArrayList<Teacher>();
        QueryResultIterator<Teacher> iterator =  query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        //Find the next cursor
        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<Teacher>builder().setItems(records).setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "insertTeacher")
    public Teacher insertTeacher(Teacher quote) throws ConflictException {
        //If if is not null, then check if it exists. If yes, throw an Exception
        //that it is already present
        if (quote.getId() != null) {
            if (findRecord(quote.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        //Since our @Id field is a Long, Objectify will generate a unique value for us
        //when we use put
        ofy().save().entity(quote).now();
        return quote;
    }

    @ApiMethod(name = "updateTeacher")
    public Teacher updateTeacher(Teacher quote)throws NotFoundException {
        if (findRecord(quote.getId()) == null) {
            throw new NotFoundException("Teacher Record does not exist");
        }
        ofy().save().entity(quote).now();
        return quote;
    }

    @ApiMethod(name = "removeTeacher")
    public void removeTeacher(@Named("id") Long id) throws NotFoundException {
        Teacher record = findRecord(id);
        if(record == null) {
            throw new NotFoundException("Teacher Record does not exist");
        }
        ofy().delete().entity(record).now();
    }

    //Private method to retrieve a <code>Teacher</code> record
    private Teacher findRecord(Long id) {
        return ofy().load().type(Teacher.class).id(id).now();
        //or return ofy().load().type(Teacher.class).filter("id",id).first.now();
    }

}