INSERT INTO usuario (username, password_hash, nombres, rol, activo) VALUES
('admin',     'pbkdf2$210000$1ff33229ece10c33cededc2e08cae952$514cb128e3319518e697ec06ff4e391f2f813bbd2020487c92fa118298da4b5e', 'Administrador del sistema', 'ADMINISTRADOR', TRUE),
('gerente',   'pbkdf2$210000$3161222df9e977e18a4aba73cc862f4f$63f180b6492d639dab80a27195f6adfa0dde01ed06fe19803728af9e11f6ddc6', 'Rosa Medina Paredes',       'GERENTE',       TRUE),
('agente',    'pbkdf2$210000$748b22a9fbcea6811ec81acbba3f03d7$caecb051962b7d6a256b74d54b8a44a3c0e0dcf0f3826843b025826c773ff40b', 'Carlos Huaman Flores',      'AGENTE',        TRUE),
('asistente', 'pbkdf2$210000$e305eb6b43bb070b1915b6b37457895c$f16704ec0483a48204cdf68a61f3322d05d2d60bdbe65f12e65a18ae398fc1a0', 'Lucia Ccama Torres',        'ASISTENTE',     TRUE);

INSERT INTO propietario (nombres, dni, telefono, email) VALUES
('Jorge Salas Valdivia',   '40123456', '959123456', 'jsalas@correo.pe'),
('Maria Zegarra Rivera',   '41234567', '958234567', 'mzegarra@correo.pe'),
('Inversiones Misti E.I.R.L. (Pedro Linares)', '42345678', '957345678', 'contacto@misti.pe');

INSERT INTO inmueble (tipo, operacion_ofrecida, metraje, distrito, direccion, precio, habitaciones, piso, cochera, acabados, referencias, fecha_construccion, id_propietario, estado) VALUES
('CASA',            'VENTA',    180.00, 'Cayma',        'Av. Cayma 512',                 480000.00, 4, 1,  TRUE,  'Porcelanato, cocina equipada', 'Cerca al C.C. Real Plaza', '2015-03-10', 1, 'NUEVO_INGRESO'),
('DEPARTAMENTO',    'ALQUILER',  85.00, 'Yanahuara',    'Calle Jerusalen 210 Dpto 302',    1800.00, 2, 3,  TRUE,  'Piso laminado',                'Frente al mirador',        '2019-08-01', 2, 'NUEVO_INGRESO'),
('DEPARTAMENTO',    'VENTA',    110.00, 'Selva Alegre', 'Av. Independencia 1020 Dpto 501', 320000.00, 3, 5,  FALSE, 'Ventanas termoacusticas',      'A dos cuadras del parque', '2021-01-15', 2, 'EN_RESERVA'),
('LOCAL_COMERCIAL', 'ALQUILER',  60.00, 'Cercado',      'Calle Mercaderes 118',             3500.00, 0, 1,  FALSE, 'Vitrina de vidrio templado',   'Zona comercial',           '2005-06-20', 3, 'NUEVO_INGRESO'),
('OFICINA',         'ALQUILER',  45.00, 'Jose Luis Bustamante y Rivero', 'Av. Dolores 305 Of. 402', 1500.00, 0, 4, TRUE, 'Aire acondicionado', 'Edificio corporativo', '2018-11-30', 3, 'NUEVO_INGRESO'),
('TERRENO',         'VENTA',    300.00, 'Sachaca',      'Urb. Campina Paisajista Lt. 12', 210000.00, NULL, NULL, FALSE, NULL,               'Con servicios basicos',    NULL,         1, 'NUEVO_INGRESO'),
('CASA',            'VENTA',    150.00, 'Yanahuara',    'Pasaje Sillar 45',              395000.00, 3, 1,  TRUE,  'Fachada de sillar',            'Zona monumental',          '1998-04-12', 2, 'VENDIDO'),
('DEPARTAMENTO',    'ALQUILER',  70.00, 'Cayma',        'Av. Ejercito 890 Dpto 201',       1400.00, 2, 2,  FALSE, 'Cocina abierta',               'Cerca a universidades',    '2020-02-28', 1, 'ALQUILADO');

INSERT INTO historial_estado (id_inmueble, estado_anterior, estado_nuevo, fecha, id_usuario) VALUES
(1, NULL, 'NUEVO_INGRESO', '2026-08-03 09:15:00', 4),
(2, NULL, 'NUEVO_INGRESO', '2026-08-05 10:00:00', 4),
(3, NULL, 'NUEVO_INGRESO', '2026-08-10 11:20:00', 4),
(3, 'NUEVO_INGRESO', 'EN_RESERVA', '2026-09-18 16:40:00', 3),
(4, NULL, 'NUEVO_INGRESO', '2026-08-12 08:30:00', 4),
(5, NULL, 'NUEVO_INGRESO', '2026-08-20 15:00:00', 4),
(6, NULL, 'NUEVO_INGRESO', '2026-09-01 09:00:00', 4),
(7, NULL, 'NUEVO_INGRESO', '2026-07-15 10:10:00', 4),
(7, 'NUEVO_INGRESO', 'VENDIDO', '2026-09-05 17:30:00', 3),
(8, NULL, 'NUEVO_INGRESO', '2026-07-20 12:00:00', 4),
(8, 'NUEVO_INGRESO', 'ALQUILADO', '2026-10-01 11:45:00', 3);

INSERT INTO operacion (id_inmueble, tipo_operacion, cliente_nombre, cliente_dni, id_agente, fecha_inicio, fecha_fin, monto, comision) VALUES
(7, 'VENTA',    'Andrea Quispe Mamani', '72345678', 3, '2026-09-05', NULL,         395000.00, 11850.00),
(8, 'ALQUILER', 'Luis Apaza Condori',   '73456789', 3, '2026-10-01', '2027-09-30',   1400.00,  1400.00);

INSERT INTO pedido (tipo_operacion, tipo_propiedad, distrito, precio_maximo, metraje_minimo, habitaciones_minimas, nombre_contacto, email, telefono) VALUES
('ALQUILER', 'CASA', 'Sachaca', 2500.00, 120.00, 3, 'Fiorella Chavez Ramos', 'fchavez@correo.pe', '987654321');

INSERT INTO solicitud_captacion (operacion_ofrecida, tipo, metraje, distrito, direccion, precio, habitaciones, piso, cochera, acabados, referencias, fecha_construccion, propietario_nombres, propietario_dni, propietario_telefono, propietario_email) VALUES
('VENTA',    'CASA',         210.00, 'Cerro Colorado', 'Calle Los Pinos 140',         520000.00, 4, 1, TRUE,  'Pisos de porcelanato', 'Cerca al aeropuerto', '2017-05-20', 'Hector Paredes Lazo', '43456789', '956456789', 'hparedes@correo.pe'),
('ALQUILER', 'DEPARTAMENTO',  65.00, 'Yanahuara',      'Av. Ejercito 1210 Dpto 402',   1600.00, 2, 4, FALSE, 'Cocina equipada',      'Frente a parque',     '2022-03-01', 'Maria Zegarra Rivera', '41234567', '958234567', 'mzegarra@correo.pe');
