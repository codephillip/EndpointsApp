package com.codephillip.backend;

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
@Api(name = "studentEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.codephillip.com", ownerName = "backend.codephillip.com", packagePath=""))
public class StudentEndpoint {

    // Make sure to add this endpoint to your web.xml file if this is a web application.

    private static final Logger LOG = Logger.getLogger(StudentEndpoint.class.getName());

    public StudentEndpoint() {
    }

    /**
     * Return a collection of quotes
     *
     * @param count The number of quotes
     * @return a list of Students
     */
    @ApiMethod(name = "listStudent")
    public CollectionResponse<Student> listStudent(@Nullable @Named("cursor") String cursorString,
                                               @Nullable @Named("count") Integer count) {

        Query<Student> query = ofy().load().type(Student.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Student> records = new ArrayList<Student>();
        QueryResultIterator<Student> iterator =  query.iterator();
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
        return CollectionResponse.<Student>builder().setItems(records).setNextPageToken(cursorString).build();
    }

    /**
     * This inserts a new <code>Student</code> object.
     * @param quote The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertStudent")
    public Student insertStudent(Student quote) throws ConflictException {
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

    /**
     * This updates an existing <code>Student</code> object.
     * @param quote The object to be added.
     * @return The object to be updated.
     */
    @ApiMethod(name = "updateStudent")
    public Student updateStudent(Student quote)throws NotFoundException {
        if (findRecord(quote.getId()) == null) {
            throw new NotFoundException("Student Record does not exist");
        }
        ofy().save().entity(quote).now();
        return quote;
    }

    /**
     * This deletes an existing <code>Student</code> object.
     * @param id The id of the object to be deleted.
     */
    @ApiMethod(name = "removeStudent")
    public void removeStudent(@Named("id") Long id) throws NotFoundException {
        Student record = findRecord(id);
        if(record == null) {
            throw new NotFoundException("Student Record does not exist");
        }
        ofy().delete().entity(record).now();
    }

    //Private method to retrieve a <code>Student</code> record
    private Student findRecord(Long id) {
        return ofy().load().type(Student.class).id(id).now();
        //or return ofy().load().type(Student.class).filter("id",id).first.now();
    }

}