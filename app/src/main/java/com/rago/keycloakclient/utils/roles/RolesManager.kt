package com.rago.keycloakclient.utils.roles

import android.util.Log

class RolesManager {
    private val myRoles: MutableList<RolesEnum> = mutableListOf()
    private val myActions: MutableMap<String, ActionsEnum> = mutableMapOf()

    companion object {
        private const val TAG = "RolesManager"
    }

    fun destroy() {
        myRoles.clear()
        myActions.clear()
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
                    myActions[rol.trim()] = ActionsEnum.VIEW
                if (rol.contains("created"))
                    myActions[rol.trim()] = ActionsEnum.CREATED
                if (rol.contains("delete"))
                    myActions[rol.trim()] = ActionsEnum.DELETE
                if (rol.contains("update"))
                    myActions[rol.trim()] = ActionsEnum.UPDATE
            }
        }

        myRoles.forEach {
            Log.i(TAG, "build: $it")
        }
        myActions.forEach { (s, actionsEnum) ->
            Log.i(TAG, "build: $s: $actionsEnum")
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
        val nonRoleActions = myActions.filterKeys {
            it.contains(rol.toString().lowercase())
        }
        return when {
            myRoles.contains(RolesEnum.ADMIN) -> true
            myRoles.contains(rol) -> {
                val actions = myActions.filterKeys {
                    it.contains(rol.toString().lowercase())
                }
                actions.containsValue(action)
            }
            nonRoleActions.containsValue(action) -> true
            else -> false
        }
    }
}