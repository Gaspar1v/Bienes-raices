INSERT INTO usuario (username, password_hash, nombres, rol, activo) VALUES
('admin',     'pbkdf2$210000$1ff33229ece10c33cededc2e08cae952$514cb128e3319518e697ec06ff4e391f2f813bbd2020487c92fa118298da4b5e', 'Administrador del sistema', 'ADMINISTRADOR', TRUE),
('gerente',   'pbkdf2$210000$3161222df9e977e18a4aba73cc862f4f$63f180b6492d639dab80a27195f6adfa0dde01ed06fe19803728af9e11f6ddc6', 'Rosa Medina Paredes',       'GERENTE',       TRUE),
('agente',    'pbkdf2$210000$748b22a9fbcea6811ec81acbba3f03d7$caecb051962b7d6a256b74d54b8a44a3c0e0dcf0f3826843b025826c773ff40b', 'Carlos Huaman Flores',      'AGENTE',        TRUE),
('asistente', 'pbkdf2$210000$e305eb6b43bb070b1915b6b37457895c$f16704ec0483a48204cdf68a61f3322d05d2d60bdbe65f12e65a18ae398fc1a0', 'Lucia Ccama Torres',        'ASISTENTE',     TRUE);
