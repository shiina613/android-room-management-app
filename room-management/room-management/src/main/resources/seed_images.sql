-- Chạy script này để cập nhật imageUrl cho các phòng đã có trong DB
-- Path tương đối — backend tự ghép base URL khi trả về

SET @BASE = '/uploads/rooms/';

UPDATE rooms SET image_url = CONCAT(@BASE, 'room_studio_1.jpg')    WHERE title = 'Phòng 101';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_studio_2.jpg')    WHERE title = 'Phòng 102';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_studio_3.jpg')    WHERE title = 'Phòng 103';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_apartment_1.jpg') WHERE title = 'Phòng 201';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_apartment_2.jpg') WHERE title = 'Phòng 202';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_apartment_3.jpg') WHERE title = 'Phòng 203';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_single_1.jpg')    WHERE title = 'Phòng 301';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_single_2.jpg')    WHERE title = 'Phòng 302';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_single_1.jpg')    WHERE title = 'Phòng 303';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_penthouse.jpg')   WHERE title = 'Phòng 401';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_apartment_1.jpg') WHERE title = 'Phòng 402';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_studio_2.jpg')    WHERE title = 'Phòng 403';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_single_2.jpg')    WHERE title = 'Phòng 501';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_single_1.jpg')    WHERE title = 'Phòng 502';
UPDATE rooms SET image_url = CONCAT(@BASE, 'room_apartment_3.jpg') WHERE title = 'Phòng 503';

SELECT id, title, image_url FROM rooms ORDER BY title;
