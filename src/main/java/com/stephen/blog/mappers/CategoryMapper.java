package com.stephen.blog.mappers;

import com.stephen.blog.domain.PostStatus;
import com.stephen.blog.domain.entities.Category;
import com.stephen.blog.domain.entities.Post;
import com.stephen.blog.dtos.CategoryDto;
import com.stephen.blog.dtos.CreateCategoryRequest;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount",source = "posts",qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts){
        if(null==posts){
            return 0;
        }
        return posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }

}
