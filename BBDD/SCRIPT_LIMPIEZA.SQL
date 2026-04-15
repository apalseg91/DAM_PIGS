-- 🔥 Cerrar sesiones activas del usuario (MUY IMPORTANTE) 
BEGIN FOR r IN ( SELECT sid, serial# FROM v$session WHERE username = 'FITMANAGE' ) LOOP EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || r.sid || ',' || r.serial# || ''' IMMEDIATE'; END LOOP; END; / 
-- 🔥 Borrar usuario (esto elimina TODO: tablas, triggers, secuencias…) 
BEGIN EXECUTE IMMEDIATE 'DROP USER FITMANAGE CASCADE'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -1918 THEN -- usuario no existe 
RAISE; END IF; END; / 
-- 🔥 Borrar tablespace (incluyendo datafile físico) 
BEGIN EXECUTE IMMEDIATE 'DROP TABLESPACE FITMANAGE INCLUDING CONTENTS AND DATAFILES'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -959 THEN 
-- tablespace no existe 
RAISE; END IF; END; / 
-- 🔥 Borrar profile 
BEGIN EXECUTE IMMEDIATE 'DROP PROFILE FITMANAGE'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2380 THEN 
-- profile no existe 
RAISE; END IF; END; 
