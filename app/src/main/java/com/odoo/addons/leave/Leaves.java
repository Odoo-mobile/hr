/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 18/12/15 12:09 PM
 */
package com.odoo.addons.leave;

public class Leaves {
    public static final String TAG = Leaves.class.getSimpleName();

    private String LeaveTitle;
    private String LeaveDateFrom;
    private String LeaveDateTo;
    private int LeaveDays;
    private String LeaveType;
    private String LeaveDescription;
    private long mId;

    public Leaves(String Title,String DateFrom,String DateTo,int Days){

        this.LeaveTitle = Title;
        this.LeaveDateFrom = DateFrom;
        this.LeaveDateTo = DateTo;
        this.LeaveDays = Days;

    }

    public Leaves(String Title,String DateFrom,String DateTo){

        this.LeaveTitle = Title;
        this.LeaveDateFrom = DateFrom;
        this.LeaveDateTo = DateTo;

    }

    public Leaves() {

    }

    public String getLeaveDateFrom() {
        return LeaveDateFrom;
    }

    public void setLeaveDateFrom(String leaveDateFrom) {
        LeaveDateFrom = leaveDateFrom;
    }

    public String getLeaveDateTo() {
        return LeaveDateTo;
    }

    public void setLeaveDateTo(String leaveDateTo) {
        LeaveDateTo = leaveDateTo;
    }

    public int getLeaveDays() {
        return LeaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        LeaveDays = leaveDays;
    }

    public String getLeaveDescription() {
        return LeaveDescription;
    }

    public void setLeaveDescription(String leaveDescription) {
        LeaveDescription = leaveDescription;
    }

    public String getLeaveTitle() {
        return LeaveTitle;
    }

    public void setLeaveTitle(String leaveTitle) {
        LeaveTitle = leaveTitle;
    }

    public String getLeaveType() {
        return LeaveType;
    }

    public void setLeaveType(String leaveType) {
        LeaveType = leaveType;
    }
}
