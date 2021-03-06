class CreateFlags < ActiveRecord::Migration
  def change
    create_table :flags do |t|
      t.integer :user_id
      t.integer :prestige

      t.timestamps
    end

    #fix only int PK -> 64bit
    change_column :flags, :id, :primary_key, :limit => 8

  end
end
