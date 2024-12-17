package com.hcc.mappers;

import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.Assignment;
import com.hcc.enums.AssignmentStatusEnum;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = AssignmentStatusEnum.class)
public interface AssignmentMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAssignment(AssignmentDto dto, @MappingTarget Assignment assignment);

    @Mapping(target = "status", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLearnerFields(AssignmentDto dto, @MappingTarget Assignment source);

    @Mapping(target = "reviewVideoUrl", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReviewerFields(AssignmentDto dto, @MappingTarget Assignment source);

    AssignmentDto toDto(Assignment assignment);

    Assignment toAssignment(AssignmentDto dto);

    List<AssignmentDto> toDtoList(List<Assignment> assignments);

    List<Assignment> toAssignmentList(List<AssignmentDto> assignmentDtos);

    AssignmentCreateDto toCreateDto(Assignment assignment);

    Assignment toAssignment(AssignmentCreateDto dto);
}
