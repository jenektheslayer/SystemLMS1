package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.dao.GroupRepository;
import org.example.dao.StudentRepository;
import org.example.dto.GroupRequest;
import org.example.dto.GroupResponse;
import org.example.dto.PagedResponse;
import org.example.mapper.GroupMapper;
import org.example.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;

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
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
        groupMapper.updateEntity(request, group);

        group = groupRepository.save(group);
        GroupResponse response = groupMapper.toDto(group);

        return response;
    }

    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
        if (studentRepository.existsByGroupId(id)) {
            throw new GroupHasStudentsException("Нельзя удалить группу, в ней есть студенты");
        }
        groupRepository.delete(group);
    }
}