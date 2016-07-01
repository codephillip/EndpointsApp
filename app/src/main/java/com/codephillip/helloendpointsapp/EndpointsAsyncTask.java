package com.codephillip.helloendpointsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.codephillip.backend.studentEndpoint.StudentEndpoint;
import com.codephillip.backend.studentEndpoint.model.Student;
import com.codephillip.backend.teacherEndpoint.TeacherEndpoint;
import com.codephillip.backend.teacherEndpoint.model.Teacher;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class EndpointsAsyncTask extends AsyncTask<Void, Void, List<Student>> {
    private static final String TAG = EndpointsAsyncTask.class.getSimpleName();
    private static StudentEndpoint studentApiService = null;
    private static TeacherEndpoint teacherApiService = null;
    private Context context;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Student> doInBackground(Void... params) {
        if(studentApiService == null) {
            //Only do this once
            //devappserver
            StudentEndpoint.Builder builder = new StudentEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://192.168.1.128:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            TeacherEndpoint.Builder builder2 = new TeacherEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://192.168.1.128:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            //online server
//            StudentEndpoint.Builder builder = new StudentEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
//                    .setRootUrl("https://endpointsapp-1007.appspot.com/_ah/api/");

            studentApiService = builder.build();
            teacherApiService = builder2.build();
        }

        try {
            //delete first item in server DB
            //List<Student> z = studentApiService.listStudent().execute().getItems();
            //long id = z.get(0).getId();
            //studentApiService.removeStudent(id).execute();
            //delete
            //studentApiService.removeStudent(5076495651307520L).execute();
            //update
            //String x = String.valueOf(studentApiService.updateStudent(new Student().setId(5066549580791808L).setWho("mememe").setWhat("deleted and gone")).execute());
            //post
            //String x = String.valueOf(studentApiService.insertStudent(new Student().setWho("Makarov2").setWhat("Best in Call of duty")).execute());
            //multiple post
            Student student = new Student();
            Teacher teacher = new Teacher();
            for (int i = 0; i < 4; i++){
                student.setName("Makarov"+i).setAge(22+i).setSubject("BCSC"+i).setUniversity("MUK"+i);
                teacher.setName("Yuri"+i).setAge(42+i).setSubject("BCSC"+i).setUniversity("MUK"+i).setSalary(32000f).setTeachingYears(12+i);
                studentApiService.insertStudent(student).execute();
                teacherApiService.insertTeacher(teacher).execute();
            }

            //Log.d(TAG, "doInBackground: "+x);
            return null;
//            return studentApiService.listStudent().execute().getItems();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<Student> result) {
//        for (Student q : result) {
//            Toast.makeText(context, q.getName() + " : " + q.getSubject(), Toast.LENGTH_LONG).show();
//        }
        Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
    }
}