package uz.kundalik.telegram.service;

import uz.kundalik.site.enums.Role;

public interface RoleService {
    Role getUserRole(Long chatId);
}
