package facci.pc.mel.thiefselfie;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import javax.mail.internet.AddressException;



/**
 * Created by edgar on 04/04/2015.
 */
public class SendEmail extends AsyncTask<String,Void,Boolean>   {
    @Override
    protected Boolean doInBackground(String... data) {

        String emailSenderAddress=(String)data[0];
        String emailSenderPassword=(String)data[1];
        String recipients=(String)data[2];
        String subject=(String)data[3];
        String comments=(String)data[4];
        String pictureFileName=(String)data[5];

        Email m = new Email(emailSenderAddress,emailSenderPassword);

        m.setTo(recipients);
        m.setFrom(emailSenderAddress);
        m.setSubject(subject);
        m.setBody(comments);



        ///m.addAttachment("/sdcard/filelocation");
        m.setPictureFileName(pictureFileName);
        try {
            return m.send();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
