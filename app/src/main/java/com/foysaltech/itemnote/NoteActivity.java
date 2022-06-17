package com.foysaltech.itemnote;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NoteActivity extends AppCompatActivity {
    public static final long NEW_NOTE = -1;
    public static final String ID = "ID";
    private DatabaseHelper db;
    private Note note;
    private EditText editTitle;
    private EditText editContent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        context = this;

        db = new DatabaseHelper(context);

        connectView();
        getInfo();
    }

    /**
     * conect with xml view
     */
    private void connectView() {
        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);
    }

    /**
     * get info note to display
     */
    private void getInfo() {
        long id = getIntent().getLongExtra(ID, NEW_NOTE);

        // not new note then find note from database by id of note
        if (id != NEW_NOTE) {
            String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NOTE + " WHERE " + DatabaseHelper.KEY_ID_NOTE + " = " + id;
            note = db.getNote(sql);
        }

        if (note != null) {
            editTitle.setText(note.getTitle());
            editContent.setText(note.getContent());
        } else {
            editTitle.setText("");
            editContent.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                break;
            case R.id.menu_delete:
                delete();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void save() {

        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        String notify = null;

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            notify = "note empty, don't save!";
        } else {

            // get curren time for last modified
            SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currenTime = formatTime.format(cal.getTime());

            // new note
            if (note == null) {
                Note note = new Note();
                note.setTitle(title).setContent(content).setLastModified(currenTime);
                if (db.insertNote(note) > 0) {
                    notify = "add success!";
                } else {
                    notify = "add fail!";
                }
            } else { // update note
                note.setTitle(title).setContent(content).setLastModified(currenTime);
                if (db.updateNote(note)) {
                    notify = "update success!";
                } else {
                    notify = "update fail!";
                }
            }
        }

        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
        finish();
    }
    private void delete() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            finish();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.delete).setIcon(R.mipmap.ic_launcher)
                    .setMessage("Do you want delete note?");
            builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteNote();
                }
            });
            builder.show();
        }
    }
    private void deleteNote() {
        if (note != null) {
            String where = DatabaseHelper.KEY_ID_NOTE + " = " + note.getId();
            String notify = "delete success!";

            if (!db.deleteNote(where)) {
                notify = "delete failt!";
            }
            Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    @Override
    public void onBackPressed() {
        save();
    }

}
