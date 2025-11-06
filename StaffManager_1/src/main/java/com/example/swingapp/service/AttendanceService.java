package com.example.swingapp.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import com.example.swingapp.dao.AttendanceDAO;
import com.example.swingapp.dao.EmployeeDAO;
import com.example.swingapp.dao.OTJunctionDAO;
import com.example.swingapp.model.DayWorkStatus;
import com.example.swingapp.model.WorkDetail;
import com.example.swingapp.model.WorkSchedule;

public class AttendanceService {
	private final AttendanceDAO dao = new AttendanceDAO();
	private final EmployeeDAO employeeDao = new EmployeeDAO();
	private final OTJunctionDAO otJunctionDao = new OTJunctionDAO();

	// 🔹 Cache dữ liệu theo tháng (RAM)
	private final Map<String, Object[][]> monthCache = new HashMap<>();

	// ------------------- HEADER -------------------
	public Vector<String> buildAttendanceHeader(int year, int month) {
		var headers = new Vector<String>();

		headers.add("STT");
		headers.add("Mã NV");
		headers.add("Tên nhân viên");
		headers.add("Chức vụ");
		headers.add("Nhà hàng");
		headers.add("restaurant_id");

		var yearMonth = YearMonth.of(year, month);
		for (var day = 1; day <= yearMonth.lengthOfMonth(); day++) {
			headers.add(String.format("%02d/%02d", day, month));
		}

		headers.add("Tổng giờ đi trễ");
		headers.add("Tổng giờ về sớm");
		headers.add("Tổng ngày nghỉ phép");
		headers.add("Tổng ngày nghỉ không phép");

		return headers;
	}

	// ------------------- LOAD DỮ LIỆU -------------------
	//	public Object[][] getAttendanceByMonth(int year, int month) {
	//		var key = year + "-" + month;
	//
	//		//Nếu đã cache, trả về luôn
	//		if (monthCache.containsKey(key)) {
	//			System.out.println("[CACHE HIT] Dữ liệu tháng " + key + " lấy từ RAM");
	//			return monthCache.get(key);
	//		}
	//
	//		System.out.println("[CACHE MISS] Đang tải dữ liệu tháng " + key + " từ DB...");
	//
	//		var employees = dao.loadBasicEmployeeData();
	//		var header = buildAttendanceHeader(year, month);
	//		var totalCols = header.size();
	//
	//		//Chỉ load work schedule 1 lần
	//		var allWorkSchedules = dao.getAllWorkSchedules(year, month);
	//
	//		//Gom theo employeeId
	//		var workMap = new HashMap<Integer, List<Object[]>>();
	//		for (var record : allWorkSchedules) {
	//			var empId = (int) record[0];
	//			workMap.computeIfAbsent(empId, k -> new ArrayList<>()).add(record);
	//		}
	//
	//		List<Object[]> rows = new ArrayList<>();
	//
	//		for (var emp : employees) {
	//			var row = new Object[totalCols];
	//			System.arraycopy(emp, 1, row, 0, 5);
	//
	//			for (var i = 5; i < totalCols - 4; i++) {
	//				row[i] = "";
	//			}
	//			row[5] = emp[6];
	//
	//			var employeeId = (int) emp[0];
	//			var works = workMap.getOrDefault(employeeId, List.of());
	//			var totalLate = 0;
	//			var totalEarly = 0;
	//			var totalLeave = 0;
	//			var totalUnpaidLeave = 0;
	//
	//			// Gom các ca theo ngày
	//			Map<Integer, List<Object[]>> dayMap = new HashMap<>();
	//			for (var record : works) {
	//				var date = (LocalDate) record[1];
	//				var day = date.getDayOfMonth();
	//				dayMap.computeIfAbsent(day, k -> new ArrayList<>()).add(record);
	//				totalLate += (int) record[2];
	//				totalEarly += (int) record[3];
	//			}
	//
	//			for (var entry : dayMap.entrySet()) {
	//				int day = entry.getKey();
	//				var colIndex = 6 + (day - 1);
	//
	//				// Vì chắc chắn mỗi ngày chỉ có 1 workschedule, ta chỉ lấy bản ghi đầu tiên/duy nhất
	//				var record = entry.getValue().get(0);
	//				// Lấy TẤT CẢ dữ liệu cần thiết từ bản ghi duy nhất này
	//				var shiftName = (String) record[8];
	//				var comeLate = (int) record[2];
	//				var earlyLeave = (int) record[3];
	//				var checkIn = (Timestamp) record[5];
	//				var checkOut = (Timestamp) record[6];
	//				var workscheduleId = (Integer) record[11]; // Lấy workscheduleId ra ngoài
	//
	//				// Khai báo kết quả
	//				var cellDisplay = "";    // Dữ liệu ca chính
	//				String otDisplay = null;    // Dữ liệu OT
	//
	//				System.out.println("Kiểm tra ID & SHIFT: WSID=" + workscheduleId + ", ShiftName=" + shiftName);
	//
	//
	//				// --- 1. XỬ LÝ CA CHÍNH (Gán vào cellDisplay) ---
	//				// Chỉ chạy nếu có Shift Name (Ca Chính)
	//				if (shiftName != null && !shiftName.isEmpty()) {
	//					String statusSymbol;
	//					if ((checkIn != null || checkOut != null) && (comeLate > 0 || earlyLeave > 0)) {
	//						statusSymbol = "T";
	//					}
	//					else if (checkIn == null && checkOut == null) {
	//						statusSymbol = "*";
	//					} else if (checkIn != null && checkOut != null) {
	//						statusSymbol = "X";
	//					} else {
	//						statusSymbol = "V";
	//					}
	//					cellDisplay = shiftName + "|" + statusSymbol;
	//				}
	//
	//				// --- 2. XỬ LÝ OT (Gán vào otDisplay) ---
	//				// LUÔN chạy kiểm tra nếu workscheduleId tồn tại, dù có Ca Chính hay không!
	//				if(workscheduleId != null){
	//					var otRecords = otJunctionDao.getFullOtByWorkScheduleId(workscheduleId);
	//					var totalOtRecords = otRecords.size();
	//
	//					if (totalOtRecords > 0) {
	//						var completeOtChecks = 0;
	//						var checkedOtRecords = 0;
	//						final var CHECK_IN_INDEX = 3;
	//						final var CHECK_OUT_INDEX = 4;
	//
	//						for (Object[] otRecord : otRecords) {
	//							var otCheckIn = (Timestamp) otRecord[CHECK_IN_INDEX];
	//							var otCheckOut = (Timestamp) otRecord[CHECK_OUT_INDEX];
	//
	//							if (otCheckIn != null && otCheckOut != null) {
	//								completeOtChecks++;
	//								checkedOtRecords++;
	//							} else if (otCheckIn != null || otCheckOut != null) {
	//								checkedOtRecords++;
	//							}
	//						}
	//						String otStatusKey;
	//						if (completeOtChecks == totalOtRecords) {
	//							otStatusKey = "X";
	//						} else if (checkedOtRecords > 0) {
	//							otStatusKey = "V";
	//						} else {
	//							otStatusKey = "*";
	//						}
	//						otDisplay = "OT|" + otStatusKey;
	//					}
	//				}
	//
	//				// --- 3. GÁN KẾT QUẢ CUỐI CÙNG (ƯU TIÊN CA CHÍNH) ---
	//				if (!cellDisplay.isEmpty()) {
	//					// Ưu tiên 1: Có Ca Chính -> Hiển thị Ca Chính
	//					row[colIndex] = cellDisplay;
	//					System.out.println("FINAL: SHIFT " + cellDisplay);
	//				} else if (otDisplay != null) {
	//					// Ưu tiên 2: KHÔNG có Ca Chính, nhưng có OT -> Hiển thị OT
	//					row[colIndex] = otDisplay;
	//					System.out.println("FINAL: OT " + otDisplay);
	//				} else {
	//					// Ưu tiên 3: Không có gì cả
	//					row[colIndex] = "";
	//				}
	//			}
	//			row[totalCols - 4] = totalLate;
	//			row[totalCols - 3] = totalEarly;
	//			row[totalCols - 2] = totalLeave;
	//			row[totalCols - 1] = totalUnpaidLeave;
	//
	//			rows.add(row);
	//		}
	//
	//		var data = rows.toArray(new Object[0][]);
	//
	//		// lưu cache vào RAM
	//		monthCache.put(key, data);
	//
	//		return data;
	//	}
	public Object[][] getAttendanceByMonth(int year, int month) {
		var key = year + "-" + month;

		if (monthCache.containsKey(key)) {
			System.out.println("[CACHE HIT] Dữ liệu tháng " + key + " lấy từ RAM");
			return monthCache.get(key);
		}

		System.out.println("[CACHE MISS] Đang tải dữ liệu tháng " + key + " từ DB...");

		// ----------------------------------------------------
		// --- BƯỚC MỚI 1: TẢI VÀ CHUẨN BỊ DỮ LIỆU OT (BULK LOAD) ---
		// ----------------------------------------------------
		// ⚠️ Đã loại bỏ truy vấn lặp đi lặp lại trong vòng lặp chính

		// Định nghĩa các Index dựa trên cấu trúc Object[] trả về từ getAllOtRecordsForMonth
		final var OT_WS_ID_IDX = 1;      // work_schedule_id (Dùng làm Key Map)
		final var OT_CHECK_IN_IDX = 3;   // ot_check_in_time
		final var OT_CHECK_OUT_IDX = 4;  // ot_check_out_time

		// Tải TẤT CẢ các bản ghi OT cho tháng chỉ trong 1 LẦN TRUY VẤN
		var allOtRecordsForMonth = otJunctionDao.getAllOtRecordsForMonth(year, month);

		// Tạo Map để tra cứu nhanh: Map<WorkScheduleId, List<Object[]>>
		Map<Integer, List<Object[]>> otMapByWsId = new HashMap<>();

		for (var otRecord : allOtRecordsForMonth) {
			var wsId = (Integer) otRecord[OT_WS_ID_IDX];
			otMapByWsId.computeIfAbsent(wsId, k -> new ArrayList<>()).add(otRecord);
		}
		// ----------------------------------------------------

		var employees = dao.loadBasicEmployeeData();
		var header = buildAttendanceHeader(year, month);
		var totalCols = header.size();

		// Chỉ load work schedule 1 lần
		var allWorkSchedules = dao.getAllWorkSchedules(year, month);

		// Gom theo employeeId
		var workMap = new HashMap<Integer, List<Object[]>>();
		for (var record : allWorkSchedules) {
			var empId = (int) record[0];
			workMap.computeIfAbsent(empId, k -> new ArrayList<>()).add(record);
		}

		List<Object[]> rows = new ArrayList<>();

		// Bắt đầu lặp qua TỪNG NHÂN VIÊN
		for (var emp : employees) {
			var row = new Object[totalCols];
			System.arraycopy(emp, 1, row, 0, 5);

			for (var i = 5; i < totalCols - 4; i++) {
				row[i] = "";
			}
			row[5] = emp[6];

			var employeeId = (int) emp[0];
			var works = workMap.getOrDefault(employeeId, List.of());
			var totalLate = 0;
			var totalEarly = 0;
			var totalLeave = 0;
			var totalUnpaidLeave = 0;

			// Gom các ca theo ngày
			Map<Integer, List<Object[]>> dayMap = new HashMap<>();
			for (var record : works) {
				var date = (LocalDate) record[1];
				var day = date.getDayOfMonth();
				dayMap.computeIfAbsent(day, k -> new ArrayList<>()).add(record);
				totalLate += (int) record[2];
				totalEarly += (int) record[3];
			}

			// Bắt đầu lặp qua TỪNG NGÀY CÓ CHẤM CÔNG (Vòng lặp có độ trễ cao nhất)
			for (var entry : dayMap.entrySet()) {
				int day = entry.getKey();
				var colIndex = 6 + (day - 1);

				// Vì chắc chắn mỗi ngày chỉ có 1 workschedule, ta chỉ lấy bản ghi đầu tiên/duy nhất
				var record = entry.getValue().get(0);

				// Lấy TẤT CẢ dữ liệu cần thiết từ bản ghi duy nhất này
				var shiftName = (String) record[8];
				var comeLate = (int) record[2];
				var earlyLeave = (int) record[3];
				var checkIn = (Timestamp) record[5];
				var checkOut = (Timestamp) record[6];
				var workscheduleId = (Integer) record[11]; // Lấy workscheduleId ra ngoài

				// Khai báo kết quả
				var cellDisplay = "";    // Dữ liệu ca chính
				String otDisplay = null;    // Dữ liệu OT

				// ❌ Loại bỏ log lặp lại: System.out.println("Kiểm tra ID & SHIFT: WSID=" + workscheduleId + ", ShiftName=" + shiftName);


				// --- 1. XỬ LÝ CA CHÍNH (Gán vào cellDisplay) ---
				if (shiftName != null && !shiftName.isEmpty()) {
					String statusSymbol;
					if ((checkIn != null || checkOut != null) && (comeLate > 0 || earlyLeave > 0)) {
						statusSymbol = "T";
					}
					else if (checkIn == null && checkOut == null) {
						statusSymbol = "*";
					} else if (checkIn != null && checkOut != null) {
						statusSymbol = "X";
					} else {
						statusSymbol = "V";
					}
					cellDisplay = shiftName + "|" + statusSymbol;
				}

				// --- 2. XỬ LÝ OT (Gán vào otDisplay) ---
				if(workscheduleId != null){
					// ✅ Tối ưu hóa: Thay thế truy vấn DB bằng TRA CỨU MAP trong RAM
					var otRecords = otMapByWsId.getOrDefault(workscheduleId, List.of());
					var totalOtRecords = otRecords.size();

					if (totalOtRecords > 0) {
						var completeOtChecks = 0;
						var checkedOtRecords = 0;
						// Sử dụng Index đã định nghĩa ở Bước 1
						final var CHECK_IN_INDEX = OT_CHECK_IN_IDX;
						final var CHECK_OUT_INDEX = OT_CHECK_OUT_IDX;

						for (Object[] otRecord : otRecords) {
							var otCheckIn = (Timestamp) otRecord[CHECK_IN_INDEX];
							var otCheckOut = (Timestamp) otRecord[CHECK_OUT_INDEX];

							if (otCheckIn != null && otCheckOut != null) {
								completeOtChecks++;
								checkedOtRecords++;
							} else if (otCheckIn != null || otCheckOut != null) {
								checkedOtRecords++;
							}
						}
						String otStatusKey;
						if (completeOtChecks == totalOtRecords) {
							otStatusKey = "X";
						} else if (checkedOtRecords > 0) {
							otStatusKey = "V";
						} else {
							otStatusKey = "*";
						}
						otDisplay = "OT|" + otStatusKey;
					}
				}

				// --- 3. GÁN KẾT QUẢ CUỐI CÙNG (ƯU TIÊN CA CHÍNH) ---
				if (!cellDisplay.isEmpty()) {
					row[colIndex] = cellDisplay;
					// ❌ Loại bỏ log lặp lại: System.out.println("FINAL: SHIFT " + cellDisplay);
				} else if (otDisplay != null) {
					row[colIndex] = otDisplay;
					// ❌ Loại bỏ log lặp lại: System.out.println("FINAL: OT " + otDisplay);
				} else {
					row[colIndex] = "";
				}
			}

			// Cập nhật tổng cuối hàng
			row[totalCols - 4] = totalLate;
			row[totalCols - 3] = totalEarly;
			row[totalCols - 2] = totalLeave;
			row[totalCols - 1] = totalUnpaidLeave;

			rows.add(row);
		}

		var data = rows.toArray(new Object[0][]);

		// lưu cache vào RAM
		monthCache.put(key, data);

		return data;
	}
	public List<WorkSchedule> getWorkSchedules(int employeeId, int year, int month, int day) {
		var targetDate = LocalDate.of(year, month, day);

		// Lấy tất cả work schedules của tháng
		var allSchedules = dao.getAllWorkSchedules(year, month);

		List<WorkSchedule> schedules = allSchedules.stream()
				.map(arr -> {
					var empId = (int) arr[0];
					var workDate = (LocalDate) arr[1];
					var comeLateFlag = (Integer) arr[2];
					var earlyLeaveFlag = (Integer) arr[3];
					var timeWork = ((Integer) arr[4]).doubleValue(); // nếu DB là int
					var checkInTs = (java.sql.Timestamp) arr[5];
					var checkOutTs = (java.sql.Timestamp) arr[6];

					// An toàn: chỉ đánh dấu trễ/ về sớm nếu có check-in/check-out
					var comeLate = checkInTs != null && comeLateFlag != null && comeLateFlag != 0;
					var earlyLeave = checkOutTs != null && earlyLeaveFlag != null && earlyLeaveFlag != 0;

					return new WorkSchedule(
							0, // id không dùng
							empId,
							null, // shiftId chưa dùng
							java.sql.Date.valueOf(workDate),
							comeLate,
							earlyLeave,
							0, // absentId chưa dùng
							timeWork,
							0, // totalOt chưa dùng
							checkInTs != null ? new java.sql.Time(checkInTs.getTime()) : null,
									checkOutTs != null ? new java.sql.Time(checkOutTs.getTime()) : null
							);
				})
				// Lọc đúng nhân viên và ngày
				.filter(ws -> ws.getEmployeeId() == employeeId && ws.getWorkDate().toLocalDate().equals(targetDate))
				.collect(Collectors.toList());

		return schedules;
	}



	// ------------------- XÓA CACHE -------------------
	public void clearCache() {
		monthCache.clear();
		System.out.println("[CACHE CLEARED] Đã xóa cache RAM.");
	}
	public void clearCache(int year, int month) {
		var key = year + "-" + month;
		if (monthCache.containsKey(key)) {
			monthCache.remove(key);
			System.out.println("[CACHE CLEARED] Đã xóa cache tháng " + key);
		}
	}

	public List<DayWorkStatus> getDayWorkStatus(String employeeName, String date) {
		var shifts = dao.getShiftsForEmployee(employeeName, date);
		List<DayWorkStatus> result = new ArrayList<>();

		for (String shift : shifts) {
			var wd = dao.getWorkDetail(employeeName, date, shift);
			var present = wd != null && wd.getCheckIn() != null && wd.getCheckOut() != null;
			result.add(new DayWorkStatus(shift, present));
		}

		return result;
	}


	// ------------------- HÀM KHÁC -------------------
	public List<String> getShiftsForEmployee(String employeeName, String date){
		return dao.getShiftsForEmployee(employeeName, date);
	}
	public List<Object[]> getShiftsForEmployeeById(int employeeId, int year, int month, int day) {
		var date = String.format("%04d-%02d-%02d", year, month, day);
		var emp = employeeDao.getById(employeeId);
		if (emp == null) {
			return List.of();
		}

		var shiftsStr = dao.getShiftsForEmployee(emp.getName(), date);
		List<Object[]> shifts = new ArrayList<>();
		for (String s : shiftsStr) {
			shifts.add(new Object[]{s});
		}
		return shifts;
	}


	public WorkDetail getWorkDetail(String employeeName, String date, String shiftName) {
		return dao.getWorkDetail(employeeName, date, shiftName);
	}

	public int getEmployeeIdByName(String name) {
		return (employeeDao.getEmployeeIdByName(name));
	}
	public int checkWorkScheduleId(int employeeId, String currentDate) {
		return dao.hasWorkSchedule(employeeId, currentDate);
	}
	public int checkShiftId(int employeeId, String currentDate) {
		return dao.hasShiftId(employeeId, currentDate);
	}
	public WorkSchedule getWorkSheduleByIdDate(int employee_id, String currentDate ) {
		return dao.getWorkScheduleByIdDate(employee_id, currentDate);
	}
}
