package com.rago.keycloakclient.utils.roles

class RolesManager {
    private val myRoles: MutableList<RolesEnum> = mutableListOf()
    private val myActions: MutableMap<String, ActionsEnum> = mutableMapOf()

    companion object {
        private const val TAG = "RolesManager"
    }

    fun build(roles: List<String>) {
        myRoles.clear()
        myActions.clear()
        roles.forEach { rol ->
            if (rol.contains("r_admin")) {
                myRoles.add(RolesEnum.ADMIN)
            }

            if (rol.contains("r_shipper")) {
                myRoles.add(RolesEnum.SHIPPER)
            }

            if (rol.contains("r_booking")) {
                myRoles.add(RolesEnum.BOOKING)
            }
            val c = rol.trim()[0]
            if (c == 'a') {
                if (rol.contains("view"))
                    myActions[rol] = ActionsEnum.VIEW
                if (rol.contains("created"))
                    myActions[rol] = ActionsEnum.CREATED
                if (rol.contains("delete"))
                    myActions[rol] = ActionsEnum.DELETE
                if (rol.contains("update"))
                    myActions[rol] = ActionsEnum.UPDATE
            }
        }
    }

    fun permissionToView(rol: RolesEnum): Boolean =
        iHavePermission(rol, ActionsEnum.VIEW)

    fun permissionToUpdateOrDelete(rol: RolesEnum): Boolean =
        iHavePermission(rol, ActionsEnum.DELETE) || iHavePermission(rol, ActionsEnum.UPDATE)

    fun permissionToCreated(rol: RolesEnum): Boolean =
        iHavePermission(rol, ActionsEnum.CREATED)

    fun permissionToUpdate(rol: RolesEnum): Boolean =
        iHavePermission(rol, ActionsEnum.UPDATE)

    fun permissionToDelete(rol: RolesEnum): Boolean =
        iHavePermission(rol, ActionsEnum.DELETE)

    private fun iHavePermission(rol: RolesEnum, action: ActionsEnum): Boolean {
        return when {
            myRoles.contains(RolesEnum.ADMIN) -> true
            myRoles.contains(rol) -> {
                val actions = myActions.filterKeys {
                    it.contains(rol.toString().lowercase())
                }
                actions.containsValue(action)
            }
            else -> false
        }
    }
}