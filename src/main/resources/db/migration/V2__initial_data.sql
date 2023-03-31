-- INGREDIENT UNITS
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_fb9a7cd0-36fe-4fb8-92f1-41683c420e36', 'Teelöffel', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_71b575ad-548e-4dbd-9036-f18977d9a689', 'Esslöffel', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_bc84ba25-2eb6-4434-92fd-33b12d12b168', 'Prise', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_5c2c23c2-f086-462c-a012-2e0529a7bbc4', 'Glas', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_86acc1e4-c763-474c-9143-b4013ccb7756', 'Dose', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c', 'Stück', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_89ff16f6-e271-4563-8592-49c7f439e300', 'Kilogramm', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_c49ef910-0ae4-4c2b-9764-88d628b767ab', 'Gramm', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_882306c7-306b-4c42-b26c-6e298d95e624', 'Blätter', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_ecfc599d-b142-46b9-8fa5-555840317337', 'ml', now());
INSERT INTO recipy.ingredient_units (ingredient_unit_id, name, created)
VALUES ('ingredient_unit_7b47a4b1-305f-4848-b93b-98458a9621a1', 'Würfel', now());

-- INGREDIENTS
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_ccc129d4-81c7-45f5-b2e8-8f6f9038985c', 'Kartoffeln', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_ffafbb8c-ff6d-499c-9ae7-cab69013708d', 'Karotten', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_b87a200a-a0e2-4948-aa2f-6671a8ecd57a', 'Mais', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_ff310a50-d073-459a-8612-60f1af178bb9', 'Sahne', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_d636b2b1-fc14-477e-8eb6-275d730ffd59', 'Salz', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_31afb24f-e0eb-466e-88f6-9c17ea4ae3db', 'Pfeffer', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_c37523cd-cba9-498a-8947-b0acaddab3cd', 'Butter', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_2312d88d-bcb0-4727-8ccd-6af2fb6225f7', 'Vollkorn Dinkelmehl', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_c8e9da44-9bf4-4005-9510-0521d5e3cf4b', 'Dinkelmehl', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_244fb39d-8dbf-4008-9a30-e5b90787724d', 'Wasser', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_8ba55248-2039-4c7a-99a2-00916c00fd6d', 'Avocado', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_c732de08-df22-403c-8f97-7f6d70732bb9', 'Paprika', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_fb890e9d-75c5-416a-b209-a0277a35365c', 'Quinoa', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_e2c2a187-9e51-4a86-be44-be3922181556', 'Minze', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_217cb18f-1004-4026-8b92-8a83eac03c16', 'Tomaten', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_496dca44-1a3c-4e74-80fa-94eb532b14fd', 'Gurke', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_9bc8f0df-df11-4d73-a4b0-02e76989d292', 'Olivenöl', now());
INSERT INTO recipy.ingredients (ingredient_id, name, created)
VALUES ('ingredient_11c7436a-c8cb-47c5-bf00-816f1ae21a90', 'Hefe', now());

-- RECIPES
INSERT INTO recipy.recipes (recipe_id, name, creator, created)
VALUES ('recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', 'Kartoffelsalat', 'initialDataUser', now());
INSERT INTO recipy.recipes (recipe_id, name, creator, created)
VALUES ('recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', 'Karottensalat', 'initialDataUser', now());
INSERT INTO recipy.recipes (recipe_id, name, creator, created)
VALUES ('recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', 'Quinoasalat', 'initialDataUser', now());
INSERT INTO recipy.recipes (recipe_id, name, creator, created)
VALUES ('recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', 'Pizza-Teig', 'initialDataUser', now());

-- INGREDIENT USAGES
-- Kartoffelsalat
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_57a32bc7-7a73-4a8a-886e-d98ff886448a', 10,
        'ingredient_ccc129d4-81c7-45f5-b2e8-8f6f9038985c',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_e9c1bc82-a2c6-4114-bda9-bcbff0340910', 1,
        'ingredient_d636b2b1-fc14-477e-8eb6-275d730ffd59',
        'ingredient_unit_fb9a7cd0-36fe-4fb8-92f1-41683c420e36',
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_3603ba3c-5892-4d99-a64a-d77fd2936311', 0.5,
        'ingredient_496dca44-1a3c-4e74-80fa-94eb532b14fd',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
-- Karottensalat
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_6e883a09-5a34-4328-ad8f-53032c60c384', 8,
        'ingredient_ffafbb8c-ff6d-499c-9ae7-cab69013708d',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_d9cf5133-2d0d-4836-a827-598ba3f17ba2', 0.5,
        'ingredient_b87a200a-a0e2-4948-aa2f-6671a8ecd57a',
        'ingredient_unit_86acc1e4-c763-474c-9143-b4013ccb7756',
        'recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_f11b8f17-cf23-403d-a217-e4452a74d058', 3,
        'ingredient_9bc8f0df-df11-4d73-a4b0-02e76989d292',
        'ingredient_unit_71b575ad-548e-4dbd-9036-f18977d9a689',
        'recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', now());
-- Quinoasalat
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_7ab06725-e712-456f-8e98-3ca4bdaaabe6', 250,
        'ingredient_fb890e9d-75c5-416a-b209-a0277a35365c',
        'ingredient_unit_c49ef910-0ae4-4c2b-9764-88d628b767ab',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_52da620e-c6f0-4f20-b919-e6a20156cd5a', 20,
        'ingredient_e2c2a187-9e51-4a86-be44-be3922181556',
        'ingredient_unit_882306c7-306b-4c42-b26c-6e298d95e624',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_c4ab7691-efae-4e17-aa29-e2d8886d74ef', 1,
        'ingredient_8ba55248-2039-4c7a-99a2-00916c00fd6d',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_9ef3b34d-8b90-4103-97c0-24f751b21061', 0.5,
        'ingredient_496dca44-1a3c-4e74-80fa-94eb532b14fd',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_1a04044f-60e7-4405-b63c-702273ad674c', 2,
        'ingredient_c732de08-df22-403c-8f97-7f6d70732bb9',
        'ingredient_unit_1068b1c1-165d-4728-a179-3acf1e4db78c',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_f6c8503b-1c07-490a-ad5d-d0d7e8460cac', 2,
        'ingredient_31afb24f-e0eb-466e-88f6-9c17ea4ae3db',
        'ingredient_unit_bc84ba25-2eb6-4434-92fd-33b12d12b168',
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
-- Pizza-Teig
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_efca12e8-e8fe-4aa9-ae12-98df0ac70ac1', 300,
        'ingredient_244fb39d-8dbf-4008-9a30-e5b90787724d',
        'ingredient_unit_ecfc599d-b142-46b9-8fa5-555840317337',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_deb72108-8029-4620-b775-2686f4bac218', 300,
        'ingredient_2312d88d-bcb0-4727-8ccd-6af2fb6225f7',
        'ingredient_unit_c49ef910-0ae4-4c2b-9764-88d628b767ab',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_869c2fb5-86a3-4cc5-9e40-56201c75b31a', 300,
        'ingredient_c8e9da44-9bf4-4005-9510-0521d5e3cf4b',
        'ingredient_unit_c49ef910-0ae4-4c2b-9764-88d628b767ab',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_948a59d8-428a-4f58-b8c2-7c4c975a0de1', 1,
        'ingredient_11c7436a-c8cb-47c5-bf00-816f1ae21a90',
        'ingredient_unit_7b47a4b1-305f-4848-b93b-98458a9621a1',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_6eb4c1e0-208e-48cf-8439-3df04ecdb511', 4,
        'ingredient_9bc8f0df-df11-4d73-a4b0-02e76989d292',
        'ingredient_unit_71b575ad-548e-4dbd-9036-f18977d9a689',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.ingredient_usages (ingredient_usage_id, amount, ingredient_id,
                                      ingredient_unit_id, recipe_id, created)
VALUES ('ingredient_usage_1ce43bbf-9943-4089-b040-bbce0c479f04', 10,
        'ingredient_d636b2b1-fc14-477e-8eb6-275d730ffd59',
        'ingredient_unit_c49ef910-0ae4-4c2b-9764-88d628b767ab',
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());

-- PREPARATION STEPS
-- Kartoffelsalat
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_d426e40e-ef0c-411b-9063-3dd687893678',
        'Die Kartoffeln in Scheiben schneiden und kochen bis sie durch sind. Danach die Kartoffeln beiseite stellen und abkühlen lassen.',
        1,
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_30ce0b06-2fc4-4b30-8f3d-e9d789ddf151',
        'Die Gurke in feine Scheiben schneiden und mit den abgekühlten Kartoffeln vermengen.', 2,
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_83b59a2a-5d1f-46fd-a7d3-e96b8f2953d0',
        'Mit dem Salz abschmecken und mindestens 3 Stunden im Kühlschrank ziehen lassen.', 3,
        'recipe_c9a3dee2-cac5-4547-bd4f-955f5ba1eca5', now());
-- Karottensalat
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_43711dee-b2f6-4fc3-9594-e7eb9c785241',
        'Die Karrotten waschen und anschließend mit einer Reibe grob raspeln.', 1,
        'recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_5c93fbc7-5e44-439d-a2f6-6293aa884d6a',
        'Alle Zutaten miteinander vermengen und direkt servieren.', 2,
        'recipe_7a84327e-8aab-4b4e-b08f-6cca50b9279e', now());
-- Quinoasalat
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_38008a1e-06db-4493-9a50-09b63f16c472',
        'Den Quinoa nach Packungsanleitung kochen und abkühlen lassen', 1,
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_7487d11e-cf95-4a3a-86e8-e3d24ad0b565',
        'Das ganze Gemüse klein schneiden und in einer Schüssel mit dem kalten Quinoa vermischen',
        2,
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_b9a5de24-65c3-4d75-8f6c-7af672c1eaee',
        'Mit dem Olivenöl, Salz und Pfeffer abschmecken und direkt servieren.',
        3,
        'recipe_a1885b4c-fe88-4f5d-91fe-742432db15c2', now());
-- Pizzateig
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_c3a6c673-adcc-4b56-85d2-4aaaaea13a41',
        'Den Hefewürfel im lauwarmen Wasser auflösen. Das Mehl und das Salz in einer großen Schüssel vermengen.',
        1,
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_99d29260-fd48-4080-a93c-934686c4df7b',
        'Aus dem Hefewasser und der Mehlmischung einen glatten Teig kneten. Hier sollte man mindestens 5 Minuten kneten!',
        2,
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());
INSERT INTO recipy.preparation_steps (preparation_step_id, description, step_number, recipe_id,
                                      created)
VALUES ('preparation_step_5aa6e9b1-63ba-4741-832f-fd4e4522b212',
        'Das Olivenöl auf den Teigballen geben und erneut kneten bis das Olivenöl im Teig aufgenommen wurde.',
        3,
        'recipe_9d483aab-6435-4741-9e88-89cf5b5d8d7c', now());

