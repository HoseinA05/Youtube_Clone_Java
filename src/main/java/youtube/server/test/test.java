package youtube.server.test;

import youtube.server.database.model.UserModel;

import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws Exception {
        System.out.println("running query");
        /// TODO: testing other models

        //users
        System.out.println(UserModel.createUser("dasdasd","asdasd","1234"));
        //System.out.println(UserModel.deleteUser(1,"1234"));
        //System.out.println(UserModel.createUser("asd","asdasd","1234"));
        //System.out.println(UserModel.getUserByPassword("asdasd","12345"));
        //System.out.println(UserModel.editUser("asdasd","12345","a","a","a",16,"12345"));
    }
}
