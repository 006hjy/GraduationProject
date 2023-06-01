package com.hexin.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import java.sql.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("黄同学的毕业设计")
        }
        //客户端请求格式:http://localhost:8080/login?username=johndoe&password=mypassword
        get("/login") {
            val username = call.parameters["username"]
            val password = call.parameters["password"]

            if (username != null && password != null) {
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "hjy101101000")

                // Check if user exists
                var stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")
                stmt.setString(1, username)

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    // User exists, check password
                    val storedPassword = rs.getString("password")

                    if (storedPassword == password) {
                        call.respondText("LoginSuccessful")
                    } else {
                        call.respondText("IncorrectPassword")
                    }
                } else {
                    // User does not exist, create new user
                    stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")
                    stmt.setString(1, username)
                    stmt.setString(2, password)
                    stmt.executeUpdate()
                    call.respondText("RegistrationSuccessful")
                }
                rs.close()
                stmt.close()
                conn.close()
            } else {
                call.respondText("ERROR")
            }
        }
        //客户端请求格式:http://localhost:8080/show?order=1
        get("/show") {
            val order = call.parameters["order"]?.toIntOrNull()

            if (order != null && order > 0) {
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "hjy101101000")
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM books ORDER BY book_id DESC LIMIT ${order - 1}, 1")
                if (rs.next()) {
                    val bookId = rs.getString("book_id")
                    val bookName = rs.getString("book_name")
                    val bookPrice = rs.getDouble("book_price")
                    val sellerId = rs.getInt("seller_id")
                    val bookPic = rs.getString("book_pic")
                    val bookBlurb = rs.getString("book_blurb")
                    val contact = rs.getString("contact")
                    call.respondText("$bookId\n$bookName\n$bookPrice\n$sellerId\n$bookPic\n$bookBlurb\n$contact")
                } else {
                    call.respondText("NotFound")
                }

                rs.close()
                stmt.close()
                conn.close()
            } else {
                call.respondText("Invalid order value")
            }
        }
        get("/release") {
            val username = call.parameters["username"]
            val password = call.parameters["password"]
            val bookName = call.parameters["book_name"]
            val bookPrice = call.parameters["book_price"]
            val bookPic = call.parameters["book_pic"]
            val bookBlurb = call.parameters["book_blurb"]
            val contact = call.parameters["contact"]

            // 验证用户名和密码是否正确
            val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "hjy101101000")
            val stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?")
            stmt.setString(1, username)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()

            if (!rs.next()) {
                // 用户名或密码错误，返回错误信息
                call.respondText("用户名或密码错误")
            } else {
                // 用户名和密码正确，获取用户ID，插入新书记录
                val userId = rs.getInt("user_id")
                val insertStmt =
                    conn.prepareStatement("INSERT INTO books (seller_id, book_name, book_price, book_pic, book_blurb, contact) VALUES (?, ?, ?, ?, ?, ?)")
                insertStmt.setInt(1, userId)
                insertStmt.setString(2, bookName)
                insertStmt.setBigDecimal(3, bookPrice?.toBigDecimal())
                insertStmt.setString(4, bookPic)
                insertStmt.setString(5, bookBlurb)
                insertStmt.setString(6, contact)
                insertStmt.executeUpdate()

                // 返回成功信息
                call.respondText("发布成功")
            }
        }
        get("/booknumber") {
            val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "hjy101101000")
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT COUNT(*) FROM books;")
            rs.next()
            val bookNumber = rs.getInt(1)
            call.respond(bookNumber.toString())
            rs.close()
            stmt.close()
            conn.close()
        }
        //客户端请求格式:http://localhost:8080/login?username=username&password=password&book_id=1
        get("/delete") {
            val username = call.parameters["username"]
            val password = call.parameters["password"]
            val bookId = call.parameters["book_id"]?.toIntOrNull()

            if (username == null || password == null || bookId == null) {
                call.respondText("格式错误")
            } else {
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "hjy101101000")

                // 验证账号密码是否正确
                val stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username=? AND password=?")
                stmt.setString(1, username)
                stmt.setString(2, password)
                val rs = stmt.executeQuery()

                if (!rs.next()) {
                    call.respondText("用户名或密码错误")
                } else {
                    val userId = rs.getInt("user_id")
                    val isAdmin = username == "root"

                    // 删除书籍
                    val deleteStmt =
                        conn.prepareStatement("DELETE FROM books WHERE book_id=?${if (isAdmin) "" else " AND seller_id=?"}")
                    deleteStmt.setInt(1, bookId)
                    if (!isAdmin) {
                        deleteStmt.setInt(2, userId)
                    }
                    val rows = deleteStmt.executeUpdate()

                    if (rows == 0) {
                        call.respondText("你没有删除权限")
                    } else {
                        call.respondText("删除成功")
                    }
                }

                rs.close()
                stmt.close()
                conn.close()
            }
        }
    }
}


