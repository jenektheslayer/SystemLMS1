package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.dao.GroupRepository;
import org.example.model.Group;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {
    private GroupRepository groupRepository;

    @Transactional
    public GroupResponse addGroup(GroupRequest request) {
        Group group = new Group(request.)
    }
}
