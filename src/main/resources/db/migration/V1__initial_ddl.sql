CREATE SCHEMA recipy;

-- INGREDIENT UNITS
CREATE TABLE recipy.ingredient_units
(
    ingredient_unit_id character varying(255)      NOT NULL,
    name               character varying(255)      NOT NULL,
    created            timestamp without time zone NOT NULL,
    CONSTRAINT ingredient_units_pkey PRIMARY KEY (ingredient_unit_id),
    CONSTRAINT ingredient_unit_name_unique UNIQUE (name)
);

ALTER TABLE recipy.ingredient_units
    OWNER to root;

-- INGREDIENTS
CREATE TABLE recipy.ingredients
(
    ingredient_id character varying(255)      NOT NULL,
    name          character varying(255)      NOT NULL,
    created       timestamp without time zone NOT NULL,
    CONSTRAINT ingredients_pkey PRIMARY KEY (ingredient_id),
    CONSTRAINT ingredients_name_unique UNIQUE (name)
);

ALTER TABLE recipy.ingredients
    OWNER to root;

-- RECIPE
CREATE TABLE recipy.recipes
(
    recipe_id character varying(255)      NOT NULL,
    name      character varying(255)      NOT NULL,
    creator   character varying(255)      NOT NULL,
    created   timestamp without time zone NOT NULL,
    CONSTRAINT recipes_pkey PRIMARY KEY (recipe_id),
    CONSTRAINT recipes_name_unique UNIQUE (name)
);

ALTER TABLE recipy.recipes
    OWNER to root;


-- INGREDIENT USAGES
CREATE TABLE recipy.ingredient_usages
(
    ingredient_usage_id character varying(255)      NOT NULL,
    amount              double precision            NOT NULL,
    ingredient_id       character varying(255)      NOT NULL,
    ingredient_unit_id  character varying(255)      NOT NULL,
    recipe_id           character varying(255)      NOT NULL,
    created             timestamp without time zone NOT NULL,
    CONSTRAINT ingredient_usages_pkey PRIMARY KEY (ingredient_usage_id),
    CONSTRAINT ingredient_usages_recipe_fkey FOREIGN KEY (recipe_id)
        REFERENCES recipy.recipes (recipe_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT ingredient_usages_ingredient_unit_fkey FOREIGN KEY (ingredient_unit_id)
        REFERENCES recipy.ingredient_units (ingredient_unit_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT ingredient_usages_ingredient_fkey FOREIGN KEY (ingredient_id)
        REFERENCES recipy.ingredients (ingredient_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE recipy.ingredient_usages
    OWNER to root;

-- PREPARATION STEPS
CREATE TABLE recipy.preparation_steps
(
    preparation_step_id character varying(255)      NOT NULL,
    description         character varying(255),
    step_number         integer                     NOT NULL,
    recipe_id           character varying(255)      NOT NULL,
    created             timestamp without time zone NOT NULL,
    CONSTRAINT preparation_steps_pkey PRIMARY KEY (preparation_step_id),
    CONSTRAINT preparation_steps_recipe_fkey FOREIGN KEY (recipe_id)
        REFERENCES recipy.recipes (recipe_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE recipy.preparation_steps
    OWNER to root;

-- RECIPE IMAGES
CREATE TABLE recipy.recipe_images
(
    image_id  character varying(255)      NOT NULL,
    recipe_id character varying(255)      NOT NULL,
    index     integer                     NOT NULL,
    created   timestamp without time zone NOT NULL,
    CONSTRAINT recipe_images_pkey PRIMARY KEY (image_id),
    CONSTRAINT recipe_images_recipe_fkey FOREIGN KEY (recipe_id)
        REFERENCES recipy.recipes (recipe_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE recipy.recipe_images
    OWNER to root;