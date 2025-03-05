package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insert(Comment comment);
    List<Comment> getCommentsByBookId(Long bookId);
}