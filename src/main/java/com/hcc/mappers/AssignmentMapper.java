package com.hcc.mappers;

import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.Assignment;
import com.hcc.enums.AssignmentEnum;
import com.hcc.enums.AssignmentStatusEnum;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = AssignmentStatusEnum.class)
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "reviewVideoUrl", ignore = true)
    @Mapping(target = "codeReviewer", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLearnerFields(AssignmentDto dto, @MappingTarget Assignment source);


    @Mapping(target = "number", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "githubUrl", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReviewerFields(AssignmentDto dto, @MappingTarget Assignment source);

    AssignmentDto toDto(Assignment assignment);

    Assignment toAssignment(AssignmentDto dto);

    List<AssignmentDto> toDtoList(List<Assignment> assignments);

    List<Assignment> toAssignmentList(List<AssignmentDto> assignmentDtos);

    AssignmentCreateDto toCreateDto(Assignment assignment);

    Assignment toAssignment(AssignmentCreateDto dto);
}
