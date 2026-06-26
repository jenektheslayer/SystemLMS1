package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.dao.GroupRepository;
import org.example.dao.StudentRepository;
import org.example.dto.GroupRequest;
import org.example.dto.GroupResponse;
import org.example.dto.PagedResponse;
import org.example.dto.StudentResponse;
import org.example.mapper.GroupMapper;
import org.example.model.Group;
import org.example.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {
    private GroupRepository groupRepository;
    private StudentRepository studentRepository;
    private GroupMapper groupMapper;

    @Transactional
    public GroupResponse addGroup(GroupRequest request) {
        Group group = groupMapper.toEntity(request);
        group = groupRepository.save(group);

        GroupResponse response = groupMapper.toDto(group);

        return response;
    }

    public GroupResponse getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Не найдена группа"));

        GroupResponse response = groupMapper.toDto(group);

        return response;
    }

    public PagedResponse<GroupResponse> getAllGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Group> groupPage = groupRepository.findAll(pageable);
        List<Group> groups = groupPage.getContent();
        List<GroupResponse> content = groupMapper.toDtoList(groups);
        return new PagedResponse<>(
                content,
                groupPage.getNumber(),
                groupPage.getSize(),
                groupPage.getTotalElements(),
                groupPage.getTotalPages()
        );
    }

    @Transactional
    public GroupResponse updateGroup(Long id, GroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException());
        groupMapper.updateEntity(request, group);

        group = groupRepository.save(group);
        GroupResponse response = groupMapper.toDto(group);

        return response;
    }

    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException());
        if (studentRepository.existsByGroupId(id)) {
            throw new GroupHasStudentsException("Нельзя удалить группу, в ней есть студенты");
        }
        groupRepository.delete(group);
    }
}