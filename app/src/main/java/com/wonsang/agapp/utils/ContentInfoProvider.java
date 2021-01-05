package com.wonsang.agapp.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.wonsang.agapp.model.ImageModel;
import com.wonsang.agapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ContentInfoProvider {

    public List<UserModel> getAllUsers(ContentResolver resolver) {
        List<UserModel> users = new ArrayList<>();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        //TODO Cursor Null Check
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                Cursor phoneNumCur = resolver
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
                Cursor emailCur = resolver
                        .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);

                List<String> phoneNumbers = new ArrayList<>();
                while (phoneNumCur.moveToNext()) {
                    String phoneNo = phoneNumCur.getString(phoneNumCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumbers.add(phoneNo);
                }
                String email = "None";
                if(emailCur.moveToNext()){
                    email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                users.add(new UserModel(name, phoneNumbers, photoUri, email));

                Cursor imageCur = resolver.query(photoUri, new String[]{ContactsContract.Contacts.PHOTO_URI}, null, null, null);

                phoneNumCur.close();
                imageCur.close();
            }
        }
        return users;
    }

    public List<ImageModel> getImages(ContentResolver resolver, int size, int pageSize) {
        List<ImageModel> imageModels = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        int requestSize = size + pageSize;
        Cursor cursor = resolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC limit " + requestSize);

        cursor.moveToPosition(size - 1);
        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageModels.add(new ImageModel(imageUri));
        }
        cursor.close();
        return imageModels;
    }

    public List<ImageModel> getAllImages(ContentResolver resolver) {
        List<ImageModel> imageModels = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Cursor cursor = resolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageModels.add(new ImageModel(imageUri));
        }
        cursor.close();
        return imageModels;
    }

    static String GetContactIdWithPhoneNo(ContentResolver resolver, String phoneNumber){
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
        Cursor deleteCursor = resolver.query(uri, projection, null, null, null);
        if(deleteCursor!=null){
            while(deleteCursor.moveToNext()){
                contactId = deleteCursor.getString(deleteCursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
            deleteCursor.close();
        }
        return contactId;
    }

    public boolean deleteContactInfo(ContentResolver resolver, String phoneNumber) {
        String contactId = GetContactIdWithPhoneNo(resolver, phoneNumber);
        resolver.delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.CONTACT_ID + " = " + contactId, null);
        return true;
    }


    public boolean addContactInfo(ContentResolver resolver, String name, String phoneNumber, String email) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE)
                .build());

        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
