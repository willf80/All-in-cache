package ci.innovteam.allincachelibrary;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

public final class Cache {

    public static void saveData(Context context, String key, final Object data, boolean temp) {
        synchronized (data) {
            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Enregistre l'objet dans le cache");
            if (data == null) return;
            final File file;
            if (temp) {
                file = getCacheFile(context, key);
            } else {
                file = getDataFile(context, key);
            }
            save(file, data);
        }
    }

    public static void saveExternalData(String externalPath, String key, final Object data, boolean isText) {
        synchronized (data) {
            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Enregistre l'objet dans le cache");
            if (data == null) return;
            final File file = getExternalDataFile(externalPath, key);
            if (!isText)
                save(file, data);
            else
                saveText(file, data);
        }
    }

    public static boolean deleteData(Context context, String key, boolean temp) {
        final File file;
        if (temp) {
            file = getCacheFile(context, key);
        } else {
            file = getDataFile(context, key);
        }

        if (file.delete())
            return true;
        else
            return false;
    }

    public static boolean deleteExternalData(String externalPath, String key) {
        final File file = getExternalDataFile(externalPath, key);
        if (file.delete())
            return true;
        else
            return false;
    }

    private static void saveText(File file, final Object data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter oos = new OutputStreamWriter(fos);
            oos.write(data.toString());
            oos.close();
            fos.close();

            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Cache enregistré : " + file.getAbsolutePath());

        } catch (Exception e) {

            if (BuildConfig.DEBUG)
                Log.e("Serialization", "ERREUR, Cache NON ENREGISTRE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void save(File file, final Object data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();

            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Cache enregistré : " + file.getAbsolutePath());

        } catch (Exception e) {

            if (BuildConfig.DEBUG)
                Log.e("Serialization", "ERREUR, Cache NON ENREGISTRE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object readExternalData(String externalPath, String key, boolean isText) {

        if (BuildConfig.DEBUG)
            Log.i("Serialization", "Chargement du externalStorage de la clé : " + key);
        File file = getExternalDataFile(externalPath, key);
        // Si le fichier n'existe pas dans le dossier du external
        if (!file.exists()) return null;

        try {
            FileInputStream fis = new FileInputStream(file);
            Object o = null;
            if (isText) {
                InputStreamReader isr = new InputStreamReader(fis);
                String str = "";
                int i;
                while ((i = isr.read()) != -1)
                    str += (char) i;

                o = str;
                isr.close();
            } else {
                ObjectInputStream ois = new ObjectInputStream(fis);
                o = ois.readObject();
                ois.close();
            }

            fis.close();

            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Fichier cache chargé");
            return o;

        } catch (Exception e) {

            if (BuildConfig.DEBUG)
                Log.e("Serialization", "ERREUR lors du chargement du cache : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static Object readData(Context context, String key) {

        if (BuildConfig.DEBUG)
            Log.i("Serialization", "Chargement du cache de la clé : " + key);
        File file = getCacheFile(context, key);
        // Si le fichier n'existe pas dans le dossier du cache
        // On regarde s'il existe dans le dossier data
        if (!file.exists()) {
            file = getDataFile(context, key);
            // S'il n'existe pas non plus dans le dossier data alors on retourne null
            if (!file.exists()) return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            ois.close();
            fis.close();

            if (BuildConfig.DEBUG)
                Log.i("Serialization", "Fichier cache chargé");
            return o;

        } catch (Exception e) {

            if (BuildConfig.DEBUG)
                Log.e("Serialization", "ERREUR lors du chargement du cache : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static File getCacheFile(Context context, String key) {
        try {
            return new File(context.getCacheDir().getAbsolutePath() + "/" + key);
        } catch (Exception e) {

            if (BuildConfig.DEBUG) {
                Log.e("Serialization", "ERREUR (getCacheFile) lors de l'ouverture du fichier pour la clé : " + key);
                Log.e("Serialization", e.getMessage());
            }

            e.printStackTrace();
            return null;
        }
    }

    private static File getDataFile(Context context, String key) {
        try {
            return new File(context.getFilesDir().getAbsolutePath() + "/" + key);
        } catch (Exception e) {
            Log.e("Serialization", "ERREUR (getDataFile) lors de l'ouverture du fichier pour la clé : " + key);
            Log.e("Serialization", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static File getExternalDataFile(String externalPath, String key) {
        try {
            return new File(externalPath + "/" + key);
        } catch (Exception e) {
            Log.e("Serialization", "ERREUR (getDataFile) lors de l'ouverture du fichier pour la clé : " + key);
            Log.e("Serialization", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}