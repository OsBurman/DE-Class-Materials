package com.library.mapper;

import com.library.model.Book;
import com.library.model.BookDto;

// TODO: Add @Mapper(componentModel = "spring") annotation
//       Import: org.mapstruct.Mapper
public interface BookMapper {

    // TODO: Declare: BookDto toDto(Book book);
    // MapStruct will generate the implementation — no method body needed

    // TODO: Declare: Book toEntity(BookDto dto);
    // Note: BookDto has no 'id' field, so the generated Book will have id = 0
    //       In a real project you would add @Mapping(target = "id", ignore = true)
}
